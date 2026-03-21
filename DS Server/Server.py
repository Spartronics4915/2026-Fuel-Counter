import ntcore
import time
import os
import winsound
import sys

#region Constants

robotTeamNumber = 4915
hubTeamNumber = 9999

robotPath = "IO/"
hubPath = "Scored/"

#endregion

# --- Setup Instance (Robot, A) ---
inst_a = ntcore.NetworkTableInstance.create()
inst_a.startClient4("DS Proxy Server - Robot")

# --- Setup Instance (Hub, B) ---
inst_b = ntcore.NetworkTableInstance.create()
inst_b.startClient4("DS Proxy Server - Hub")

# --- Configure Connections ---
if "sim" in sys.argv:
    sim_idx = sys.argv.index("sim")
    args = sys.argv[sim_idx + 1:]
    
    # Manual Override
    if len(args) >= 2:
        try:
            robot_port = int(args[0])
            hub_port = int(args[1])
            print(f"Manual override: Robot (4915) -> {robot_port}, Hub (9999) -> {hub_port}\n")
        except ValueError:
            print("Invalid manual ports provided.")
    else:
        print("Usage: python Server.py sim <robot_port> <hub_port>")
        exit(1)

    inst_a.setServer("localhost", robot_port)
    inst_b.setServer("localhost", hub_port)

else:
    # Physical Robot Connection
    inst_a.setServerTeam(robotTeamNumber)
    inst_b.setServerTeam(hubTeamNumber)

# Dictionaries to track publishers for each direction
pubs_on_b = {} # Data moving A -> B
pubs_on_a = {} # Data moving B -> A

def buffer():
    bufferSpeed = 0.2
    states = ["-", "\\", "|", "/"]
    return(states[round(time.time()//bufferSpeed % 4)])

# --- Function: Forward Generic ---
def forward(event, pubs_dict, dest_inst):
    # Only process value events
    if not hasattr(event.data, "value"):
        return

    try:
        topic_name = event.data.topic.getName()
        value = event.data.value
        
        # Use the same topic name on the destination
        dest_path = topic_name
        
        if dest_path not in pubs_dict:
            # Check if the topic exists or creating a new publisher is safe
            pubs_dict[dest_path] = dest_inst.getTopic(dest_path).genericPublish(event.data.topic.getTypeString())
        
        pubs_dict[dest_path].set(value)
    except Exception as e:
        print(f"Error forwarding {topic_name}: {e}\033[K")

# --- Function: Play Sound ---
soundFile = os.path.join(os.path.dirname(os.path.abspath(__file__)), "PowerUp_LinearPop.wav")

def playPowerUp(event):
    try:
        # Check if event has a value
        if not hasattr(event.data, "value"):
            return

        val = event.data.value
        if val and val.isBoolean():
            if os.path.exists(soundFile):
                winsound.PlaySound(soundFile, winsound.SND_ASYNC | winsound.SND_FILENAME)
                print("Hub Shift!\033[K")
    except Exception as e:
        print(f"Error playing sound: {e}\033[K")

# --- Attach Listeners ---
# Listen for "Hub Enabled"
# Ensure we have a valid path for the listener
hub_enabled_topic = f"{robotPath}Hub Enabled" # robotPath ends in /, so this makes /IO/Hub Enabled
inst_a.addListener([hub_enabled_topic], ntcore.EventFlags.kValueRemote | ntcore.EventFlags.kValueLocal, playPowerUp)

# Listen to Robot A's Vision folder -> Send to Robot B
inst_a.addListener([robotPath], ntcore.EventFlags.kValueRemote | ntcore.EventFlags.kValueLocal, lambda event: forward(event, pubs_on_b, inst_b))

# Listen to Robot B's Control folder -> Send to Robot A
inst_b.addListener([hubPath], ntcore.EventFlags.kValueRemote | ntcore.EventFlags.kValueLocal, lambda event: forward(event, pubs_on_a, inst_a))

print("Proxy running... Press Ctrl+C to stop.")
try:
    while True:
        status_a = "\033[42m\033[97mCONNECTED\033[0m" if inst_a.isConnected() else "\033[41m\033[97mOFFLINE\033[0m"
        status_b = "\033[42m\033[97mCONNECTED\033[0m" if inst_b.isConnected() else "\033[41m\033[97mOFFLINE\033[0m"
        print(f"Robot: {status_a} | Hub: {status_b} [{buffer()}]\033[K", end="\r")
        time.sleep(0.1)
except KeyboardInterrupt:
    print("\nStopping...")
    # Force exit immediately to avoid hanging on stopClient() if connection is unstable
    os._exit(0)
    exit(0)

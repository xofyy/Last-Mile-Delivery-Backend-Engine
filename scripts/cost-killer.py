import subprocess
import json
from datetime import datetime

def run_gcloud_command(command):
    try:
        result = subprocess.run(command, shell=True, capture_output=True, text=True)
        if result.returncode != 0:
            return []
        return json.loads(result.stdout)
    except Exception as e:
        print(f"Error running command: {e}")
        return []

def check_unused_disks():
    print("🔍 Searching for Orphaned Disks (Unused)...")
    # Disks that are NOT attached to any instance
    disks = run_gcloud_command("gcloud compute disks list --format=json")
    unused_disks = [d for d in disks if 'users' not in d or not d['users']]
    
    if unused_disks:
        print(f"⚠️ FOUND {len(unused_disks)} UNUSED DISKS (MONEY WASTER!):")
        for d in unused_disks:
            print(f"  - Name: {d['name']} | Size: {d['sizeGb']}GB | Zone: {d.get('zone', 'unknown').split('/')[-1]}")
    else:
        print("✅ No unused disks found. Clean!")

def check_unused_ips():
    print("\n🔍 Searching for Unused Static IPs...")
    # IPs that are RESERVED but not IN_USE
    ips = run_gcloud_command("gcloud compute addresses list --format=json")
    unused_ips = [ip for ip in ips if ip['status'] == 'RESERVED' and 'users' not in ip]

    if unused_ips:
        print(f"⚠️ FOUND {len(unused_ips)} UNUSED IPs:")
        for ip in unused_ips:
            print(f"  - Name: {ip['name']} | IP: {ip['address']} | Region: {ip['region'].split('/')[-1]}")
    else:
        print("✅ No unused IPs found. Clean!")

if __name__ == "__main__":
    print(f"💰 COST OPTIMIZER BOT - {datetime.now().strftime('%Y-%m-%d')}")
    print("="*50)
    check_unused_disks()
    check_unused_ips()
    print("="*50)
    print("🚀 Scan Complete.")
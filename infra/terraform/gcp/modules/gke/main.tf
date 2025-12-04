resource "google_container_cluster" "primary" {
  name     = var.cluster_name
  location = var.zone

  # We can't create a cluster with no node pool defined, but we want to only use
  # separately managed node pools. So we create the smallest possible default
  # node pool and immediately delete it.
  remove_default_node_pool = true
  initial_node_count       = 1

  network    = var.network_name
  subnetwork = var.subnet_name

  # VPC-Native Networking
  ip_allocation_policy {
    cluster_secondary_range_name  = "pod-ranges"
    services_secondary_range_name = "service-ranges"
  }

  # --- SECURITY: PRIVATE CLUSTER SETTINGS ---
  private_cluster_config {
    enable_private_nodes    = true  # Nodes will NOT have Public IPs
    enable_private_endpoint = false # Control Plane will have internet access (For easy management)
    master_ipv4_cidr_block  = var.master_ipv4_cidr_block
  }

  # --- SECURITY: ONLY ALLOWED IP'S CAN USE KUBECTL ---
  # master_authorized_networks_config {
  #   cidr_blocks {
  #     cidr_block   = "YOUR_IP_ADDRESS/32"
  #     display_name = "My Home IP"
  #   }
  # }

  # Workload Identity
  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }

  deletion_protection = false
}

resource "google_container_node_pool" "primary_nodes" {
  name       = "${var.cluster_name}-node-pool"
  location   = var.zone
  cluster    = google_container_cluster.primary.name
  node_count = var.node_count

  node_config {
    spot         = true
    machine_type = var.machine_type

    # Since it is a Private Node, it will use NAT for outbound access
    tags = ["gke-node", "${var.cluster_name}-gke"]

    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
  }
}

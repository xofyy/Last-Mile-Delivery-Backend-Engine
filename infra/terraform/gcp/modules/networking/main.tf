resource "google_compute_network" "vpc" {
  name                    = var.vpc_name
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnet" {
  name          = var.subnet_name
  region        = var.region
  network       = google_compute_network.vpc.name
  ip_cidr_range = var.subnet_cidr

  # Pod and Service ranges for IP allocation
  secondary_ip_range {
    range_name    = "pod-ranges"
    ip_cidr_range = var.pod_range_cidr
  }

  secondary_ip_range {
    range_name    = "service-ranges"
    ip_cidr_range = var.service_range_cidr
  }
}

# --- CLOUD NAT (For Internet Access of Private Nodes) ---
resource "google_compute_router" "router" {
  name    = "${var.vpc_name}-router"
  region  = var.region
  network = google_compute_network.vpc.name
}

resource "google_compute_router_nat" "nat" {
  name                               = "${var.vpc_name}-nat"
  router                             = google_compute_router.router.name
  region                             = var.region
  nat_ip_allocate_option             = "AUTO_ONLY"
  source_subnetwork_ip_ranges_to_nat = "ALL_SUBNETWORKS_ALL_IP_RANGES"
}

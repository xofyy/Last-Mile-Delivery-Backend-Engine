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

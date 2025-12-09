terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
  zone    = var.zone
}

# Enable required APIs
resource "google_project_service" "compute" {
  service            = "compute.googleapis.com"
  disable_on_destroy = false
}

resource "google_project_service" "container" {
  service            = "container.googleapis.com"
  disable_on_destroy = false
}

module "networking" {
  source = "../../modules/networking"

  project_id         = var.project_id
  region             = var.region
  vpc_name           = var.vpc_name
  subnet_name        = var.subnet_name
  subnet_cidr        = var.subnet_cidr
  pod_range_cidr     = var.pod_range_cidr
  service_range_cidr = var.service_range_cidr
}

module "gke" {
  source = "../../modules/gke"

  project_id             = var.project_id
  zone                   = var.zone
  cluster_name           = var.cluster_name
  network_name           = module.networking.network_name
  subnet_name            = module.networking.subnet_name
  node_count             = var.node_count
  min_node_count         = var.min_node_count
  max_node_count         = var.max_node_count
  machine_type           = var.machine_type
  master_ipv4_cidr_block = var.master_ipv4_cidr_block
  disk_size_gb           = var.disk_size_gb
  disk_type              = var.disk_type
}

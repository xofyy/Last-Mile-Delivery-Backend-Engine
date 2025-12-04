variable "project_id" {
  description = "The GCP Project ID"
  type        = string
}

variable "zone" {
  description = "The GCP Zone"
  type        = string
}

variable "cluster_name" {
  description = "The name of the GKE cluster"
  type        = string
}

variable "network_name" {
  description = "The name of the VPC network"
  type        = string
}

variable "subnet_name" {
  description = "The name of the subnetwork"
  type        = string
}

variable "node_count" {
  description = "Number of nodes in the node pool"
  type        = number
}

variable "machine_type" {
  description = "Machine type for the node pool"
  type        = string
}

variable "master_ipv4_cidr_block" {
  description = "The IP range in CIDR notation to use for the hosted master network"
  type        = string
  default     = "172.16.0.0/28"
}

variable "disk_size_gb" {
  description = "Node boot disk size in GB"
  default     = 50
}

variable "disk_type" {
  description = "Node boot disk type"
  default     = "pd-balanced"
}

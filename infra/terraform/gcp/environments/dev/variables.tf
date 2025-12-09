variable "project_id" {
  description = "The GCP Project ID"
  type        = string
}

variable "region" {
  description = "The GCP Region"
  type        = string
  default     = "us-central1"
}

variable "zone" {
  description = "The GCP Zone"
  type        = string
  default     = "us-central1-a"
}

variable "cluster_name" {
  description = "The name of the GKE cluster"
  type        = string
  default     = "delivery-cluster-dev"
}

variable "node_count" {
  description = "Number of nodes in the node pool"
  type        = number
  default     = 1
}

variable "min_node_count" {
  description = "Minimum number of nodes for autoscaling"
  type        = number
  default     = 1
}

variable "max_node_count" {
  description = "Maximum number of nodes for autoscaling"
  type        = number
  default     = 2 # Keep small for dev
}

variable "machine_type" {
  description = "Machine type for the node pool"
  type        = string
  default     = "e2-small" # Cost effective for dev
}

variable "vpc_name" {
  description = "Name of the VPC network"
  type        = string
  default     = "delivery-vpc-dev"
}

variable "subnet_name" {
  description = "Name of the subnet"
  type        = string
  default     = "delivery-subnet-dev"
}

variable "subnet_cidr" {
  description = "CIDR range for the subnet"
  type        = string
  default     = "10.10.0.0/24" # Different range for dev
}

variable "pod_range_cidr" {
  description = "CIDR range for pods"
  type        = string
  default     = "10.11.0.0/16"
}

variable "service_range_cidr" {
  description = "CIDR range for services"
  type        = string
  default     = "10.12.0.0/20"
}

variable "master_ipv4_cidr_block" {
  description = "The IP range in CIDR notation to use for the hosted master network"
  type        = string
  default     = "172.16.0.16/28"
}

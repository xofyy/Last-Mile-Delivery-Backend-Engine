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
  default     = "delivery-cluster"
}

variable "node_count" {
  description = "Number of nodes in the node pool"
  type        = number
  default     = 1
}

variable "machine_type" {
  description = "Machine type for the node pool"
  type        = string
  default     = "e2-medium"
}

variable "vpc_name" {
  description = "Name of the VPC network"
  type        = string
  default     = "delivery-vpc"
}

variable "subnet_name" {
  description = "Name of the subnet"
  type        = string
  default     = "delivery-subnet"
}

variable "subnet_cidr" {
  description = "CIDR range for the subnet"
  type        = string
  default     = "10.0.0.0/24"
}

variable "pod_range_cidr" {
  description = "CIDR range for pods"
  type        = string
  default     = "10.1.0.0/16"
}

variable "service_range_cidr" {
  description = "CIDR range for services"
  type        = string
  default     = "10.2.0.0/20"
}

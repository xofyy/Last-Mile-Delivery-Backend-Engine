variable "project_id" {
  description = "The project ID to host the network in"
  type        = string
}

variable "region" {
  description = "The region to host the network in"
  type        = string
}

variable "vpc_name" {
  description = "The name of the VPC network"
  type        = string
}

variable "subnet_name" {
  description = "The name of the subnetwork"
  type        = string
}

variable "subnet_cidr" {
  description = "The CIDR range for the subnetwork"
  type        = string
}

variable "pod_range_cidr" {
  description = "The CIDR range for the pods"
  type        = string
}

variable "service_range_cidr" {
  description = "The CIDR range for the services"
  type        = string
}

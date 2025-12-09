variable "project_id" {
  description = "The project ID"
  type        = string
}

variable "service_account_id" {
  description = "The ID of the Google Service Account"
  type        = string
}

variable "display_name" {
  description = "The display name of the Google Service Account"
  type        = string
}

variable "k8s_namespace" {
  description = "Kubernetes namespace where the SA resides"
  type        = string
}

variable "k8s_sa_name" {
  description = "Kubernetes Service Account name"
  type        = string
}

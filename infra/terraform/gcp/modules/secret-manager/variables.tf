variable "project_id" {
  description = "The project ID"
  type        = string
}

variable "secret_ids" {
  description = "List of secret IDs to create"
  type        = list(string)
}

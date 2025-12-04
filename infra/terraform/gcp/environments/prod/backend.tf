terraform {
  backend "gcs" {
    bucket = "delivery-tf-state"
    prefix = "environments/prod"
  }
}

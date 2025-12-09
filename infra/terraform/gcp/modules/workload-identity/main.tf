resource "google_service_account" "sa" {
  account_id   = var.service_account_id
  display_name = var.display_name
}

resource "google_project_iam_member" "secret_accessor" {
  project = var.project_id
  role    = "roles/secretmanager.secretAccessor"
  member  = "serviceAccount:${google_service_account.sa.email}"
}

resource "google_service_account_iam_binding" "workload_identity_user" {
  service_account_id = google_service_account.sa.name
  role               = "roles/iam.workloadIdentityUser"

  members = [
    "serviceAccount:${var.project_id}.svc.id.goog[${var.k8s_namespace}/${var.k8s_sa_name}]"
  ]
}

output "service_account_email" {
  value = google_service_account.sa.email
}

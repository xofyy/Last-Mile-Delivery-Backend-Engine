#!/usr/bin/env python
from constructs import Construct
from cdktf import App, TerraformStack, TerraformOutput
from cdktf_cdktf_provider_google.provider import GoogleProvider

# Import Standard Library & Configs
from infrastructure_lib import (
    StandardVPC, StandardCluster, StandardSecrets, StandardIdentity,
    NetworkConfig, ClusterConfig
)

class DeliveryStack(TerraformStack):
    def __init__(self, scope: Construct, id: str):
        super().__init__(scope, id)

        # --- Variables (Dynamic from Env) ---
        import os
        from dotenv import load_dotenv
        load_dotenv() # Load variables from .env file if present

        project_id = os.getenv("GCP_PROJECT_ID", "encoded-might-480112-k4")
        region = os.getenv("GCP_REGION", "us-central1")
        zone = os.getenv("GCP_ZONE", "us-central1-a")
        env = os.getenv("ENV", "dev")
        
        # --- Provider ---
        GoogleProvider(self, "Google",
            project=project_id,
            region=region,
            zone=zone
        )

        # --- Remote Backend (State Management) ---
        # Stores the state in a GCS bucket for team collaboration
        from cdktf import GcsBackend
        GcsBackend(self,
            bucket="delivery-terraform-state",
            prefix=f"cdktf/{env}"
        )

        # --- 1. Networking (Using Config Object) ---
        net_config = NetworkConfig(
            project_id=project_id,
            region=region,
            env=env,
            cidr="10.10.0.0/24"
        )

        vpc = StandardVPC(self, "networking", config=net_config)

        # --- 2. Compute (Using Config Object) ---
        # Try changing min_nodes to 0 or machine_type to "n1-standard-1" to see validation fail!
        cluster_config = ClusterConfig(
            project_id=project_id,
            region=region,
            zone=zone,
            env=env,
            min_nodes=1,
            max_nodes=3,
            machine_type="e2-standard-2",  # Upgraded for Istio support (8GB RAM)
            spot_instances=True 
        )

        cluster = StandardCluster(self, "compute",
            config=cluster_config,
            network_id=vpc.network.id,
            subnet_id=vpc.subnet.id
        )

        # --- 3. Security (Secrets) ---
        StandardSecrets(self, "secret_manager",
            secret_ids=[
                "postgres-password",
                "rabbitmq-password",
                "rabbitmq-user",
                "mongo-uri",
                "jwt-secret"
            ]
        )

        # --- 4. Security (Identity) ---
        StandardIdentity(self, "workload_identity",
            project_id=project_id,
            sa_id="external-secrets-sa",
            k8s_namespace="external-secrets",
            k8s_sa_name="external-secrets"
        )

        # --- Outputs ---
        TerraformOutput(self, "cluster_name", value=cluster.cluster.name)
        TerraformOutput(self, "cluster_endpoint", value=cluster.cluster.endpoint)


app = App()
DeliveryStack(app, "delivery-infra-cdktf")
app.synth()

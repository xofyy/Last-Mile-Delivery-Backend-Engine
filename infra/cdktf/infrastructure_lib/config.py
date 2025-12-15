from dataclasses import dataclass, field
from typing import List

@dataclass
class NetworkConfig:
    project_id: str
    region: str
    env: str
    cidr: str = "10.0.0.0/16"
    pod_cidr: str = "10.11.0.0/21"
    service_cidr: str = "10.12.0.0/21"

    @property
    def vpc_name(self) -> str:
        return f"delivery-{self.env}-vpc"
    
    @property
    def subnet_name(self) -> str:
        return f"delivery-{self.env}-subnet"

@dataclass
class ClusterConfig:
    project_id: str
    region: str
    zone: str
    env: str
    
    # Defaults with Validation Rules
    machine_type: str = "e2-standard-2"
    node_count: int = 1
    min_nodes: int = 1
    max_nodes: int = 3
    disk_size: int = 50
    spot_instances: bool = True
    master_cidr: str = "172.16.0.0/28"
    
    def __post_init__(self):
        # 1. Cost Control Validation (Updated)
        # Allow e2 (Cost efficient) and n2 (Balanced). Block expensive/legacy ones.
        allowed_families = ["e2-", "n2-", "t2a-"]
        if not any(family in self.machine_type for family in allowed_families):
             raise ValueError(f"Machine type {self.machine_type} is not allowed! Allowed families: {allowed_families}")

        # 2. Production Safety Validation
        if self.env == "prod":
            if self.max_nodes < 3:
                raise ValueError("Production clusters must allow scaling up to at least 3 nodes!")
            if self.spot_instances:
                # Warning: Usually prod doesn't use ONLY spot, but for this project budget it's OK.
                pass 

    @property
    def cluster_name(self) -> str:
        return f"delivery-{self.env}-cluster"

    @property
    def workload_pool(self) -> str:
        return f"{self.project_id}.svc.id.goog"

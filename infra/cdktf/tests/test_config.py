import pytest
from infrastructure_lib.config import ClusterConfig, NetworkConfig

def test_valid_cluster_config():
    """Test a valid dev configuration."""
    config = ClusterConfig(
        project_id="test-project",
        region="us-central1",
        zone="us-central1-a",
        env="dev",
        machine_type="e2-medium",
        min_nodes=1,
        max_nodes=3
    )
    assert config.cluster_name == "delivery-dev-cluster"
    assert config.workload_pool == "test-project.svc.id.goog"

def test_invalid_machine_type():
    """Test that expensive/legacy machine types are rejected."""
    with pytest.raises(ValueError) as excinfo:
        ClusterConfig(
            project_id="p", region="r", zone="z", env="dev",
            machine_type="n1-standard-1" # Legacy, should fail
        )
    assert "not allowed" in str(excinfo.value)
    
    with pytest.raises(ValueError) as excinfo:
        ClusterConfig(
            project_id="p", region="r", zone="z", env="dev",
            machine_type="c2-standard-4" # Expensive, should fail
        )
    assert "not allowed" in str(excinfo.value)

def test_prod_constraints():
    """Test that production clusters must have at least 3 max nodes."""
    with pytest.raises(ValueError) as excinfo:
        ClusterConfig(
            project_id="p", region="r", zone="z", env="prod",
            max_nodes=2 # Too small for prod
        )
    assert "scaling up to at least 3 nodes" in str(excinfo.value)

def test_allowed_machine_types():
    """Test valid machine families."""
    valid_types = ["e2-micro", "n2-standard-2", "t2a-standard-1"]
    for mt in valid_types:
        config = ClusterConfig(
            project_id="p", region="r", zone="z", env="dev",
            machine_type=mt
        )
        assert config.machine_type == mt

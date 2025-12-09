# 🏗️ Infrastructure as Code (CDKTF)

This directory contains the **Modernized Infrastructure** code for the Last-Mile Delivery Engine, built using **CDKTF (Cloud Development Kit for Terraform)** and **Python**.

It replaces HCL (HashiCorp Configuration Language) with a true programming language, enabling:
- **Abstraction:** Complex resources are hidden behind simple classes (`StandardCluster`).
- **Policy as Code:** Validation logic prevents bad configurations (e.g., expensive instances).
- **Unit Testing:** Infrastructure logic is tested before synthesis.

## 📂 Project Structure

```text
infra/cdktf/
├── main.py                 # The "Consumer" app (defines the stack)
├── infrastructure_lib/     # The "Library" (reusable modules)
│   ├── networking.py       # VPC, Subnet, NAT logic
│   ├── gke.py              # GKE Cluster logic
│   ├── security.py         # Secret Manager & Workload Identity
│   └── config.py           # Configuration objects & Validation logic
├── tests/                  # Unit tests for policy validation
├── requirements.txt        # Project dependencies
└── setup.py                # Package definition
```

## 🚀 Getting Started

### 1. Prerequisites
- Python 3.9+
- Node.js & npm (for `cdktf-cli`)
- Google Cloud SDK (`gcloud`)

### 2. Installation
Install the project dependencies and the library itself in "editable" mode:
```bash
pip install -r requirements.txt
```
*This installs `cdktf`, `pytest`, `python-dotenv`, and our local `infrastructure-lib`.*

### 3. Configuration
You can configure the environment via a `.env` file or Environment Variables.

**Option A: `.env` File (Recommended for Local)**
Copy the example and edit:
```bash
cp .env.example .env
```

**Option B: Environment Variables (CI/CD)**
```bash
export ENV=prod
export GCP_PROJECT_ID=my-project-id
export GCP_REGION=us-central1
```

## 🧪 Testing (Policy Validation)
Before generating any Terraform code, run the unit tests to ensure your configuration is compliant.

```bash
pytest
```
*Tests check for:*
- **Cost Control:** Blocks expensive machine types (e.g., `n1-standard*`, `c2-*`).
- **Production Safety:** Enforces `max_nodes >= 3` for production.

## 🔨 Synthesis & Deployment

1.  **Synthesize (Generate Terraform JSON):**
    ```bash
    cdktf synth
    # OR
    python main.py
    ```
    *Output is stored in `cdktf.out/`.*

2.  **Deploy (Apply to Cloud):**
    ```bash
    cdktf deploy
    ```

## 📚 Library Reference

### `StandardCluster`
Encapsulates a best-practice GKE Cluster.
- **Private Nodes:** Enabled by default.
- **VPC-Native:** Uses Alias IPs.
- **Workload Identity:** Enabled (`[project].svc.id.goog`).

### `StandardVPC`
Creates a custom VPC with:
- **Cloud NAT:** For private node internet access.
- **Secondary Ranges:** For Pods (`pod-ranges`) and Services (`service-ranges`).

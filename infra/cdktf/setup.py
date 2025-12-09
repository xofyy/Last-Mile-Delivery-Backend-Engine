from setuptools import setup, find_packages

setup(
    name="infrastructure-lib",
    version="0.1.0",
    packages=find_packages(),
    install_requires=[
        "cdktf",
        "cdktf-cdktf-provider-google",
        "constructs"
    ],
    description="Standard Infrastructure Library",
    author="Platform Team",
)

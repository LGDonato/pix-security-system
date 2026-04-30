from datetime import datetime

from airflow import DAG
from airflow.operators.bash import BashOperator


default_args = {
    "owner": "pix-security",
}


# This DAG orchestrates the PIX data pipeline from event simulation to Gold analytics.
with DAG(
    dag_id="pix_security_pipeline",
    default_args=default_args,
    description="Runs the PIX Security System data pipeline.",
    start_date=datetime(2026, 1, 1),
    schedule=None,
    catchup=False,
    tags=["pix-security", "medallion"],
) as dag:
    generate_events = BashOperator(
        task_id="generate_events",
        bash_command="python /opt/airflow/data_pipeline/generate_events.py --count 100",
    )

    bronze = BashOperator(
        task_id="bronze",
        bash_command="python /opt/airflow/data_pipeline/bronze.py",
    )

    silver = BashOperator(
        task_id="silver",
        bash_command="python /opt/airflow/data_pipeline/silver.py",
    )

    gold = BashOperator(
        task_id="gold",
        bash_command="python /opt/airflow/data_pipeline/gold.py",
    )

    generate_events >> bronze >> silver >> gold

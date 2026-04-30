from pathlib import Path

import pandas as pd


PROJECT_ROOT = Path(__file__).resolve().parents[1]
INPUT_FILE = PROJECT_ROOT / "data" / "events" / "pix_transactions.jsonl"
OUTPUT_FILE = PROJECT_ROOT / "data" / "bronze" / "pix_transactions.parquet"


def ingest_bronze():
    # Bronze stores raw data exactly as it arrives from the event source.
    if not INPUT_FILE.exists():
        raise FileNotFoundError(
            f"Input file not found: {INPUT_FILE}. Run data_pipeline/generate_events.py first."
        )

    dataframe = pd.read_json(INPUT_FILE, lines=True)

    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
    dataframe.to_parquet(OUTPUT_FILE, engine="pyarrow", index=False)

    print(f"Processed {len(dataframe)} records into {OUTPUT_FILE}")


def main():
    ingest_bronze()


if __name__ == "__main__":
    main()

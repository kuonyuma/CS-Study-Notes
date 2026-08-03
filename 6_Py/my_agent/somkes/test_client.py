from client.client import get_client


def main():
    client = get_client()
    model_name = "gemini-3.6-flash"
    response = client.models.generate_content(
        model=model_name, contents="你好，请介绍一下你自己"
    )
    print(response.text)


if __name__ == "__main__":
    main()

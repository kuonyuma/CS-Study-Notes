from typing import Protocol


class TextGenerator(Protocol):
    def generate_text(self, prompt: str) -> str: ...


class GptTextGenerator:
    def generate_text(self, prompt: str) -> str:
        return f"来自gpt的文本生成: {prompt}"


class LlamaTextGenerator:
    def generate_text(self, prompt: str) -> str:
        return f"来自llama的文本生成: {prompt}"


def ask(generator: TextGenerator, prompt: str) -> str:
    return generator.generate_text(prompt)


def main():
    gpt_generator = GptTextGenerator()
    llama_generator = LlamaTextGenerator()

    prompt = "请生成一段关于人工智能的介绍。"

    gpt_response = ask(gpt_generator, prompt)
    llama_response = ask(llama_generator, prompt)
    print(gpt_response)
    print(llama_response)


if __name__ == "__main__":
    main()

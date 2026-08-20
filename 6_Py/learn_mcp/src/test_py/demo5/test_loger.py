import logging
import sys


def configure_logging() -> None:
    logging.basicConfig(
        level=logging.DEBUG,
        format=("%(asctime)s %(levelname)s logger=%(name)s message=%(message)s"),
        stream=sys.stdout,
    )


def main() -> None:
    configure_logging()

    logger = logging.getLogger(__name__)

    logger.debug("调试信息")
    logger.info("程序启动")
    logger.warning("配置文件不存在，使用默认配置")
    logger.error("模型调用失败")


if __name__ == "__main__":
    main()

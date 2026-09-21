from fastapi import FastAPI, Form, UploadFile

app = FastAPI()


@app.post("/profile")
async def profile(
    avatar: UploadFile,
    username: str = Form(),
):
    return {
        "username": username,
        "filename": avatar.read,
    }


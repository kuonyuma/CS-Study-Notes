from fastapi import APIRouter

router = APIRouter(
    prefix="/routers   /users"
)

@router.get("/{user_id}")
async def user_get(user_id:int):
    return f"user_id{user_id}"
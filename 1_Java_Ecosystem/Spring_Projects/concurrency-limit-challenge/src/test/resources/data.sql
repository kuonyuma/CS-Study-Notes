INSERT INTO zhihu_keyword (keyword)
SELECT '模拟关键词_' || X
FROM SYSTEM_RANGE(1, 1000);

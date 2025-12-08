
- Среднее время ответа: 14,97 мс
- Общее время запросов: 569 мс


### CompositeStructure Controller

| Эндпоинт | Метод | Время (мс) |
|----------|-------|------------|
| `/api/comp-structures` | `GET` | 14 |
| `/api/comp-structures/1` | `GET` | 13 |
| `/api/comp-structures/comp-fun/1` | `GET` | 16 |
| `/api/comp-structures/anal-fun/1` | `GET` | 12 |
| `/api/comp-structures` | `POST` | 23 |

- Среднее время: 15,60 мс

### TabFun Controller

| Эндпоинт | Метод | Время (мс) | 
|----------|-------|------------|
| `/api/tab-fun` | `GET` | 18 |
| `/api/tab-fun/1` | `GET` | 17 |
| `/api/tab-fun/search?name=test_tabulated` | `GET` | 12 |
| `/api/tab-fun` | `POST` | 12 |

- Среднее время: 14,75 мс

### TabPoints Controller

| Эндпоинт | Метод | Время (мс) |
|----------|-------|------------|
| `/api/tab-points` | `GET` | 15 | 
| `/api/tab-points/1` | `GET` | 14 |
| `/api/tab-points/function/1` | `GET` | 19 |
| `/api/tab-points/point?x=1.0&functionId=1` | `GET` | 11 |
| `/api/tab-points` | `POST` | 17 |

- Среднее время: 15,20 мс

### CompPoints Controller

| Эндпоинт | Метод | Время (мс) | 
|----------|-------|------------|
| `/api/comp-points` | `GET` | 13 |
| `/api/comp-points/1` | `GET` | 11 | 
| `/api/comp-points/function/1` | `GET` | 13 | 
| `/api/comp-points/point?x=1.0&functionId=1` | `GET` | 13 |
| `/api/comp-points` | `POST` | 22 |

- Среднее время: 14,40 мс

### AnalFun Controller

| Эндпоинт | Метод | Время (мс) |
|----------|-------|------------|
| `/api/anal-fun` | `GET` | 20 | 
| `/api/anal-fun/1` | `GET` | 13 | 
| `/api/anal-fun` | `POST` | 16 | 

- Среднее время: 16,33 мс

### AnalPoints Controller

| Эндпоинт | Метод | Время (мс) |
|----------|-------|------------|
| `/api/anal-points` | `GET` | 13 |
| `/api/anal-points/1` | `GET` | 12 | 
| `/api/anal-points/function/1` | `GET` | 12 | 
| `/api/anal-points/point?x=1.0&functionId=1` | `GET` | 12 | 
| `/api/anal-points` | `POST` | 23 |

- Среднее время: 14,40 мс

### Users Controller

| Эндпоинт | Метод | Время (мс) | 
|----------|-------|------------|
| `/api/users` | `GET` | 15 |
| `/api/users/1` | `GET` | 14 | 
| `/api/users/search/login?login=testuser` | `GET` | 19 |
| `/api/users/search/email?email=test@example.com` | `GET` | 14 |
| `/api/users/check-login?login=testuser` | `POST` | 10 | 
| `/api/users/check-email?email=test@example.com` | `POST` | 12 |
| `/api/users/auth` | `POST` | 12 |

- Среднее время: 13,71 мс

### CompFun Controller

| Эндпоинт | Метод | Время (мс) |
|----------|-------|------------|
| `/api/comp-fun` | `GET` | 21 |
| `/api/comp-fun/1` | `GET` | 16 |
| `/api/comp-fun/search?name=test_composite` | `GET` | 12 |
| `/api/comp-fun` | `POST` | 18 |

- Среднее время: 16,75 мс

## Сводная таблица

| Контроллер | Эндпоинтов | Среднее время (мс) |
|------------|------------|------------------|
| CompositeStructure | 5 | 15,60 |
| TabFun | 4 | 14,75 |
| TabPoints | 5 | 15,20 |
| CompPoints | 5 | 14,40 |
| AnalFun | 3 | 16,33 |
| AnalPoints | 5 | 14,40 |
| Users | 7 | 13,71 |
| CompFun | 4 | 16,75 |

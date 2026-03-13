import os

env = os.environ.get('APP_ENV', 'unknown')
print(f"Current Environment is: {env}")
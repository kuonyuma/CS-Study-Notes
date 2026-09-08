def outer():
    x = 100
    y = 1
    def inner():

        print(x,y)

    return inner

f = outer()

print(f.__closure__)

print(type(f.__closure__))

print(len(f.__closure__))

print(f.__closure__[0].cell_contents)

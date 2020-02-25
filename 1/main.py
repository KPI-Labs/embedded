import math
import time
import numpy
import random
import matplotlib.pyplot as plt

n = 6
N_START = 500
N_END = 1500

w_max = 1200
w_step = w_max / n

def generate_func(i):
    φ = random.random()
    w = w_step * (i + 1)
    amplitude = random.random()
    return lambda t: amplitude * math.sin(w * t + φ)

def generate_matrix(n, start, end, funcs):
    matrix = [0] * (end - start)
    for i in range(n):
        for t in range(len(matrix)):
            matrix[t] += funcs[i](t)
    return matrix

def print_plot(a, x, deviation):
    plt.plot(a, x)
    plt.xlabel('N')
    plt.ylabel('x')
    plt.show()

funcs = [generate_func(i) for i in range(n)]
x = generate_matrix(n, N_START, N_END, funcs)
a = [numpy.mean(x[:i]) for i in range(len(x))]
print_plot(numpy.arange(N_START, N_END), a, numpy.std(x))
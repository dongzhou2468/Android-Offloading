import matplotlib.pyplot as plt

x = []
y = []
null_x = []
null_y = []

'''
with open('C:/Users/CH/Desktop/test/time_size_relation_1.csv', 'w') as f1:
    f1.write('data_size,time_cost\n')
    with open('C:/Users/CH/Desktop/test/time_size_relation_1.txt', 'r') as f:
        for line in f.readlines():
            print (line)
            arrays = line.split(',')
            for data in arrays:
                tmp = data.split(':')
                if tmp[1] == 'null':
                    null_x.append(tmp[0])
                    null_y.append('1000')
                    str=tmp[0]+',1000\n'
                    f1.write(str)
                elif int(tmp[1]) < 600:
                    x.append(tmp[0])
                    y.append(tmp[1])
                    str=tmp[0]+','+tmp[1]+'\n'
                    f1.write(str)
'''

with open('C:/Users/CH/Desktop/test/time_size_relation.txt', 'r') as f:
    for line in f.readlines():
        print (line)
        arrays = line.split(',')
        for data in arrays:
            tmp = data.split(':')
            if tmp[1] == 'null':
                null_x.append(tmp[0])
                null_y.append('1000')
            elif int(tmp[1]) < 600:
                x.append(tmp[0])
                y.append(tmp[1])
                
x = list(map(int, x))
y = list(map(int, y))
null_x = list(map(int, null_x))
null_y = list(map(int, null_y))
print (x)
print (y)
print (null_x)
print (null_y)

plt.xlabel('data_size')
plt.ylabel('time_cost')
plt.plot(x, y, 'bo', null_x, null_y, 'ro')
plt.plot([1, 20000], [67.2808882707, 539.5166753007], 'green')
plt.show()

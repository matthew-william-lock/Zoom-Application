import itertools
my_list = ['a', 'b', 'c', 'd']
combinations=[]
for x in itertools.permutations(my_list):
    combinations.append(x)
print(len(combinations))
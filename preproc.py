# Preroccess script for kmeans input datasets with non-numeric attributes
# each non-numeric att is translated into multipul columns where each column is associated with a single descreet values, and all columns get 0 except the column of the original values that gets 1. 
import csv

with open('Datasets/zomato.csv') as f:
    reader = csv.reader(f)
    data = list(reader)

city_map={}
index=0
for line in data[1:]:
    city=line[2]
    cost=line[5]
    rate=line[15]
    if city not in city_map.keys():
        city_map[city]=index
        index=index+1
with open('Datasets/zomato_for_kmeans.csv',"w") as f:
    for line in data[1:]:
        city=line[2]
        cost=line[5]
        rate=line[15]
        new_line=""
        city_i=city_map[city]
        for i in range(0,index):
            if i == city_i:
                new_line=new_line+"1,"
            else:
                new_line=new_line+"0,"
        new_line=new_line+cost+","+rate+"\n"
        f.write(new_line)
print(index)
with open('Datasets/Banks.csv') as f:
    reader = csv.reader(f)
    data = list(reader)

name_map={}
state_map={}
n_index=0
s_index=0
for line in data[1:]:
    name=line[0]
    state=line[5]
    if name not in name_map.keys():
        name_map[name]=n_index
        n_index=n_index+1
    if state not in state_map.keys():
        state_map[state]=s_index
        s_index=s_index+1
# print(name_map)
# print(state_map)
with open('Datasets/Banks_for_kmeans.csv',"w") as f:
    for line in data[1:]:
        name=line[0]
        state=line[5]
        new_line=""
        name_i=name_map[name]
        state_i=state_map[state]+n_index
        for i in range(0,n_index):
            if i == name_i:
                new_line=new_line+"1,"
            else:
                new_line=new_line+"0,"
        for i in range(n_index,n_index+s_index):
            if i == state_i:
                new_line=new_line+"1,"
            else:
                new_line=new_line+"0,"
        f.write(new_line[:-1]+"\n")
print(n_index+s_index)
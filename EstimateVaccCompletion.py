import csv
import datetime as dt
from numpy import ndarray
from sklearn import linear_model
import numpy as np

# CSE 444 -> Ravan Sadigli -> 20160807005
# Estimating Vaccine Completion
# First, it reading the CSV file and append all data to the "allData" variable.
# The user then enters the country where he wants to know the estimated completion date of the vaccine.
# And, all the requested country-related data is added to the "desiredCountry" array.
# Then, it splits the date as month and day.
# The year is not taken, because all attribute of year is same for this data set.
# So, date of the day and month, people_vaccinated_per_hundred added to the "day",
# "month", and "vaccinated" arrays, respectively. And, the array has been converted
# into NumPy array for linear regression calculation. Then setting training dataset for linear regression.
# It finds the date when the "predicted" variable is equal to 100 using the simple brute force algorithm.

allData, desiredcountry, day, month, vaccinated = [], [], [], [], []
isPredicted = False  # when date is calculated, it becomes true
estimatedMonth = 0   # estimatedMonth, estimatedDay, estimatedYear used for calculate exact date
estimatedDay = 0
estimatedYear = 2021

# opening csv file reading
with open("share-people-vaccinated-covid.csv", "r") as excel:
    country = input("Enter Country: ")
    readallData = csv.reader(excel)
    next(readallData)
    for line in readallData:
        allData.append(line)
col = [x[0] for x in allData]

# the requested country data is added to the array
if country in col:
    for x in range(0, len(allData)):
        if country == allData[x][0]:
            if desiredcountry not in allData[x]:
                desiredcountry.append(allData[x])
else:
    print("No Data found about ", country)
    exit()

# split the date into days and months and add to the arrays
for a in range(0, len(desiredcountry)):
    date = dt.datetime.strptime(desiredcountry[a][2], "%Y-%m-%d")
    day.append(date.day)
    month.append(date.month)
    vaccinated.append(desiredcountry[a][3])  # vaccination data is added to the array

# If the dataset is too large, it can be give wrong results.
# That's why n elements of the array are removed.
if len(day) > 40:
    del day[:20]
    del month[:20]
    del vaccinated[:20]

if len(day) > 20:
    del day[:5]
    del month[:5]
    del vaccinated[:5]

# arrays are converted to NumPy array for linear regression calculation.
npday = np.array(day)
npmonth = np.array(month)
mergeIndependents = np.array([npmonth, npday])
dependentY = np.array(vaccinated)
independentX: ndarray = mergeIndependents.transpose()

# training dataset
regression = linear_model.LinearRegression()
regression.fit(independentX, dependentY)

# finding the "predicted" variable where is equal to 100
while not isPredicted:
    if estimatedDay == 30:
        estimatedMonth += 1
        estimatedDay = 0
        if estimatedMonth % 12 == 0 and month != 0:
            estimatedYear += 1
    estimatedDay += 1
    predicted = regression.predict([[estimatedMonth, estimatedDay]])
    if predicted >= 99.9:
        if estimatedMonth % 12 == 0:
            estimatedMonth = 12
        else:
            estimatedMonth = estimatedMonth % 12
        print("Estimated completion date of vaccination: ", estimatedYear, estimatedMonth, estimatedDay)
        isPredicted = True

additionalInfo = input("Press enter for detailed information: ")

print("Independent variables: ", independentX)
print("Dependent variables: ", dependentY)

print("Intercept: ", regression.intercept_)
print("Coefficent of independent variables: ", regression.coef_)

def arithmetic_arranger(problems, displayAns = False):
  #check problems count
  if len(problems) > 5:
    return "Error: Too many problems."

  line1 = []
  line2 = []
  line3 = []
  for i in range(len(problems)):
    temp = problems[i].split()
    numb1 = temp[0]
    operator = temp[1]
    numb2 = temp[2]
    #check operator
    if operator in ("*", "/"):
      return "Error: Operator must be '+' or '-'."
    #check first number
    if not numb1.lstrip('-').isnumeric():
      return "Error: Numbers must only contain digits."
    #check second number
    if not numb2.lstrip('-').isnumeric():
      return "Error: Numbers must only contain digits."
    #check number of digits
    if (len(numb1) > 4) or (len(numb2) > 4):
      return "Error: Numbers cannot be more than four digits."
    
    if(len(numb1) >= len(numb2)):
      numb1 = "  " + numb1
      numb2 = " "*(len(numb1)-2-len(numb2)) + numb2
    else:
      numb1 = "  " + " "*(len(numb2)-len(numb1)) + numb1
    line1.append(numb1)
    line2.append(operator + " " + numb2)
    line3.append("-"*len(numb1))
        
  arranged_problems = ("    ".join(line1) + "\n" 
                       + "    ".join(line2) + "\n"
                       + "    ".join(line3))

  if(displayAns):
    line4 = []
    for i in range(len(problems)):
      numb1 = int(line1[i])
      if(line2[i][0] == "+"):
        numb2 = int(line2[i].lstrip("+"))
      else:
        numb2 = -int(line2[i].lstrip("-"))
      result = str(numb1+numb2)
      line4.append(" "*(len(line1[i])-len(result)) + result)
    arranged_problems += "\n" + "    ".join(line4)
  return arranged_problems

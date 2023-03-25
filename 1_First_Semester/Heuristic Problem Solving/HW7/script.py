import os
from docx2pdf import convert

# Specify the path of the directory containing the .docx files
docx_dir = "C:\Python\NYU_Subjects\1_First_Semester\Heuristic Problem Solving\HW7 .docx"

# Iterate over all the .docx files in the directory and convert them to .pdf
for filename in os.listdir(docx_dir):
    if filename.endswith(".docx"):
        input_file = os.path.join(docx_dir, filename)
        output_file = os.path.join(docx_dir, os.path.splitext(filename)[0] + ".pdf")
        convert(input_file, output_file)
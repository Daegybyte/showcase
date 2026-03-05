using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS.Controllers
{
    [Authorize(Roles = "Student")]
    public class StudentController : Controller
    {
        //If your context is named something else, fix this and the
        //constructor param
        private LMSContext db;
        public StudentController(LMSContext _db)
        {
            db = _db;
        }

        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Catalog()
        {
            return View();
        }

        public IActionResult Class(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            return View();
        }

        public IActionResult Assignment(string subject, string num, string season, string year, string cat, string aname)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            return View();
        }


        public IActionResult ClassListings(string subject, string num)
        {
            System.Diagnostics.Debug.WriteLine(subject + num);
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            return View();
        }


        /*******Begin code to modify********/

        /// <summary>
        /// Returns a JSON array of the classes the given student is enrolled in.
        /// Each object in the array should have the following fields:
        /// "subject" - The subject abbreviation of the class (such as "CS")
        /// "number" - The course number (such as 5530)
        /// "name" - The course name
        /// "season" - The season part of the semester
        /// "year" - The year part of the semester
        /// "grade" - The grade earned in the class, or "--" if one hasn't been assigned
        /// </summary>
        /// <param name="uid">The uid of the student</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetMyClasses(string uid)
        {
            var query =
                from g in db.Grades
                join cl in db.Classes on g.ClassId equals cl.ClassId
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                where uid == g.UId
                select new
                {
                    subject = cr.Subject,
                    number = cr.Number,
                    name = cr.Name,
                    season = cl.Semester,
                    year = cl.Year,
                    grade = g.Grade1
                };

            return Json(query);
        }

        /// <summary>
        /// Returns a JSON array of all the assignments in the given class that the given student is enrolled in.
        /// Each object in the array should have the following fields:
        /// "aname" - The assignment name
        /// "cname" - The category name that the assignment belongs to
        /// "due" - The due Date/Time
        /// "score" - The score earned by the student, or null if the student has not submitted to this assignment.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="uid"></param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentsInClass(string subject, int num, string season, int year, string uid)
        {
            var query =
                from asg in db.Assignments
                join asgcat in db.AssignmentCategories on asg.AcId equals asgcat.AcId
                join cl in db.Classes on asgcat.ClassId equals cl.ClassId
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                where cr.Subject == subject &&
                    cr.Number == num &&
                    cl.Semester == season &&
                    cl.Year == year
                select new
                {
                    aname = asg.Name,
                    cname = asgcat.Name,
                    due = asg.DueDate,
                    score = (from s in db.Submissions
                             where s.Student == uid &&
                                s.AId == asg.AId
                             select s.Score).FirstOrDefault()
                };

            return Json(query.ToArray());
        }



        /// <summary>
        /// Adds a submission to the given assignment for the given student
        /// The submission should use the current time as its DateTime
        /// You can get the current time with DateTime.Now
        /// The score of the submission should start as 0 until a Professor grades it
        /// If a Student submits to an assignment again, it should replace the submission contents
        /// and the submission time (the score should remain the same).
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The new assignment name</param>
        /// <param name="uid">The student submitting the assignment</param>
        /// <param name="contents">The text contents of the student's submission</param>
        /// <returns>A JSON object containing {success = true/false}</returns>
        public IActionResult SubmitAssignmentText(string subject, int num, string season, int year,
          string category, string asgname, string uid, string contents)
        {
            int changedRows = 0;

            var alreadySubmitted =
                (from s in db.Submissions
                join asg in db.Assignments on s.AId equals asg.AId
                join asgcat in db.AssignmentCategories on asg.AcId equals asgcat.AcId
                join cl in db.Classes on asgcat.ClassId equals cl.ClassId
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                where subject == cr.Subject &&
                    num == cr.Number &&
                    season == cl.Semester &&
                    year == cl.Year &&
                    category == asgcat.Name &&
                    asgname == asg.Name &&
                    uid == s.Student
                select s).FirstOrDefault();

            if (alreadySubmitted == null)
            {
                var newSubmit = new Submission()
                {
                    AId = (from asg in db.Assignments
                           join asgcat in db.AssignmentCategories on asg.AcId equals asgcat.AcId
                           join cl in db.Classes on asgcat.ClassId equals cl.ClassId
                           join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                           where subject == cr.Subject &&
                               num == cr.Number &&
                               season == cl.Semester &&
                               year == cl.Year &&
                               category == asgcat.Name &&
                               asgname == asg.Name
                           select asg.AId).FirstOrDefault(),
                    Student = uid,
                    Contents = contents,
                    Score = 0
                };
                db.Submissions.Add(newSubmit);
                changedRows = db.SaveChanges();
            }
            else
            {
                alreadySubmitted.Contents = contents;
                changedRows = db.SaveChanges();
            }

            return Json(new { success = (changedRows != 0 )});
        }


        /// <summary>
        /// Enrolls a student in a class.
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester</param>
        /// <param name="year">The year part of the semester</param>
        /// <param name="uid">The uid of the student</param>
        /// <returns>A JSON object containing {success = {true/false}. 
        /// false if the student is already enrolled in the class, true otherwise.</returns>
        public IActionResult Enroll(string subject, int num, string season, int year, string uid)
        {
            var alreadyEnrolled =
                (from g in db.Grades
                 join cl in db.Classes on g.ClassId equals cl.ClassId
                 join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                 where subject == cr.Subject &&
                     num == cr.Number &&
                     season == cl.Semester &&
                     year == cl.Year &&
                     uid == g.UId
                 select g).Any();

            if (!alreadyEnrolled)
            {
                var newEnroll = new Grade()
                {
                    ClassId = (from cl in db.Classes
                               join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                               where subject == cr.Subject &&
                                   num == cr.Number &&
                                   season == cl.Semester &&
                                   year == cl.Year
                               select cl.ClassId).FirstOrDefault(),
                    UId = uid,
                    Grade1 = "--"
                };
                db.Grades.Add(newEnroll);
                int changedRows = db.SaveChanges();
                return Json(new { success = (changedRows != 0) });
            }
            else
            {
                return Json(new { success = false });
            }

        }



        /// <summary>
        /// Calculates a student's GPA
        /// A student's GPA is determined by the grade-point representation of the average grade in all their classes.
        /// Assume all classes are 4 credit hours.
        /// If a student does not have a grade in a class ("--"), that class is not counted in the average.
        /// If a student is not enrolled in any classes, they have a GPA of 0.0.
        /// Otherwise, the point-value of a letter grade is determined by the table on this page:
        /// https://advising.utah.edu/academic-standards/gpa-calculator-new.php
        /// </summary>
        /// <param name="uid">The uid of the student</param>
        /// <returns>A JSON object containing a single field called "gpa" with the number value</returns>
        public IActionResult GetGPA(string uid)
        {

            double tempGpa = 0.0;

            var query =
                from g in db.Grades
                where uid == g.UId && g.Grade1 != "--"
                select g.Grade1;

            
            if (query != null)
            {

                double total = 0;
                int count = query.Count();

                foreach (string g in query)
                {
                    switch (g)
                    {
                        case "A":
                            total += 4.0;
                            break;
                        case "A-":
                            total += 3.7;
                            break;
                        case "B+":
                            total += 3.3;
                            break;
                        case "B":
                            total += 3.0;
                            break;
                        case "B-":
                            total += 2.7;
                            break;
                        case "C+":
                            total += 2.3;
                            break;
                        case "C":
                            total += 2.0;
                            break;
                        case "C-":
                            total += 1.7;
                            break;
                        case "D+":
                            total += 1.3;
                            break;
                        case "D":
                            total += 1.0;
                            break;
                        case "D-":
                            total += 0.7;
                            break;
                        case "E":
                            total += 0.0;
                            break;
                    }
                }

                tempGpa = total / count;
            }

            string gpa = String.Format("{0:0.0}", tempGpa);


            return Json(new { gpa = gpa});
        }

        // A	    4.0
        // A-	    3.7
        // B+	    3.3
        // B	    3.0
        // B-	    2.7
        // C+	    2.3
        // C	    2.0
        // C-	    1.7
        // D+	    1.3
        // D	    1.0
        // D-	    0.7
        // E	    0.0
                
        /*******End code to modify********/

    }
}


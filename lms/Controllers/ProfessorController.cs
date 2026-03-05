using System;
using System.Collections.Generic;
using System.Diagnostics.Metrics;
using System.Linq;
using System.Numerics;
using System.Runtime.InteropServices;
using System.Security.Cryptography;
using System.Text.Json;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json.Linq;
using static System.Formats.Asn1.AsnWriter;
using static Microsoft.EntityFrameworkCore.DbLoggerCategory;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS_CustomIdentity.Controllers
{
    [Authorize(Roles = "Professor")]
    public class ProfessorController : Controller
    {

        //If your context is named something else, fix this
        //and the constructor param
        private readonly LMSContext db;

        public ProfessorController(LMSContext _db)
        {
            db = _db;
        }

        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Students(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
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

        public IActionResult Categories(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            return View();
        }

        public IActionResult CatAssignments(string subject, string num, string season, string year, string cat)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
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

        public IActionResult Submissions(string subject, string num, string season, string year, string cat, string aname)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            return View();
        }

        public IActionResult Grade(string subject, string num, string season, string year, string cat, string aname, string uid)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            ViewData["uid"] = uid;
            return View();
        }

        /*******Begin code to modify********/


        /// <summary>
        /// Returns a JSON array of all the students in a class.
        /// Each object in the array should have the following fields:
        /// "fname" - first name
        /// "lname" - last name
        /// "uid" - user ID
        /// "dob" - date of birth
        /// "grade" - the student's grade in this class
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <returns>The JSON array</returns>

        public IActionResult GetStudentsInClass(string subject, int num, string season, int year)
        {
            var query =
                from s in db.Students
                join g in db.Grades on s.UId equals g.UId
                join cl in db.Classes on g.ClassId equals cl.ClassId
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                where cr.Subject == subject &&
                    cr.Number == num &&
                    cl.Semester == season &&
                    cl.Year == year
                select new
                {
                    fname = s.FirstName,
                    lname = s.LastName,
                    uid = s.UId,
                    dob = s.Dob.ToString(),
                    grade = g.Grade1
                };

        
            //var query = from st in db.Students
            //            join en in db.Grades on st.UId equals en.UId
            //            join cl in db.Classes on en.ClassId equals cl.ClassId
            //            join c in db.Courses on cl.CatalogId equals c.CatalogId
            //            join d in db.Departments on c.Name equals d.Name
            //            where d.Subject == subject &&
            //                c.Number == num &&
            //                cl.Semester == season &&
            //                cl.Year == year
            //            select new
            //            {
            //                fname = st.FirstName,
            //                lname = st.LastName,
            //                uid = st.UId,
            //                dob = st.Dob,
            //                grade = en.Grade1
            //            };

            return Json(query.ToArray());
        }



        /// <summary>
        /// Returns a JSON array with all the assignments in an assignment category for a class.
        /// If the "category" parameter is null, return all assignments in the class.
        /// Each object in the array should have the following fields:
        /// "aname" - The assignment name
        /// "cname" - The assignment category name.
        /// "due" - The due DateTime
        /// "submissions" - The number of submissions to the assignment
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class, 
        /// or null to return assignments from all categories</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentsInCategory(string subject, int num, string season, int year, string category)
        {
            //Console.WriteLine("\n\n\n\nStarted GetAssignmentsInCategory\n\n\n\n");

            if (category == null)
            {
                //Console.WriteLine("\n\n\n\nCategory null\n\n\n\n");

                var query = from a in db.Assignments
                            join ac in db.AssignmentCategories on a.AcId equals ac.AcId
                            join c in db.Classes on ac.ClassId equals c.ClassId
                            join cr in db.Courses on c.CatalogId equals cr.CatalogId
                            where cr.Subject == subject &&
                                cr.Number == num &&
                                c.Semester == season &&
                                c.Year == year
                            select new
                            {
                                aname = a.Name,
                                cname = ac.Name,
                                due = a.DueDate.ToString(),
                                submissions = (from s in db.Submissions
                                               where s.AId == a.AId
                                               select s).Count(),
                            };

                return Json(query.ToArray());
            }
            else
            {
                //Console.WriteLine("\n\n\n\nCategory NOT null\n\n\n\n");

                var query = from a in db.Assignments
                            join ac in db.AssignmentCategories on a.AcId equals ac.AcId
                            join c in db.Classes on ac.ClassId equals c.ClassId
                            join cr in db.Courses on c.CatalogId equals cr.CatalogId
                            where cr.Subject == subject &&
                                cr.Number == num &&
                                c.Semester == season &&
                                c.Year == year &&
                                ac.Name == category
                            select new
                            {
                                aname = a.Name,
                                cname = ac.Name,
                                due = a.DueDate.ToString(),
                                submissions = (from s in db.Submissions
                                               where s.AId == a.AId
                                               select s).Count(),
                            };

                return Json(query.ToArray());
            }  
        }


        /// <summary>
        /// Returns a JSON array of the assignment categories for a certain class.
        /// Each object in the array should have the folling fields:
        /// "name" - The category name
        /// "weight" - The category weight
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentCategories(string subject, int num, string season, int year)
        {

            //Console.WriteLine("\n\n\n\nStarted GetAssignmentCategories\n\n\n\n");


            var query = 
                    from ac in db.AssignmentCategories
                    join c in db.Classes on ac.ClassId equals c.ClassId
                    join cr in db.Courses on c.CatalogId equals cr.CatalogId
                    where cr.Subject == subject &&
                        cr.Number == num &&
                        c.Semester == season &&
                        c.Year == year
                    select new
                    {
                        name = ac.Name,
                        weight = ac.GradingWeight,

                    };

            return Json(query.ToArray());
        }

        /// <summary>
        /// Creates a new assignment category for the specified class.
        /// If a category of the given class with the given name already exists, return success = false.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The new category name</param>
        /// <param name="catweight">The new category weight</param>
        /// <returns>A JSON object containing {success = true/false} </returns>
        public IActionResult CreateAssignmentCategory(string subject, int num, string season, int year, string category, int catweight)
        {
            var classID =
                (from cl in db.Classes
                 join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                 where cr.Subject == subject &&
                     cr.Number == num &&
                     cl.Semester == season &&
                     cl.Year == year
                 select cl.ClassId).FirstOrDefault();


            var newAsgCat = new AssignmentCategory()
            {
                GradingWeight = (uint)catweight,
                Name = category,
                ClassId = classID
            };
            db.AssignmentCategories.Add(newAsgCat);
            int changedRows = db.SaveChanges();
            return Json(new { success = (changedRows != 0) });

        }

        /// <summary>
        /// Creates a new assignment for the given class and category.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The new assignment name</param>
        /// <param name="asgpoints">The max point value for the new assignment</param>
        /// <param name="asgdue">The due DateTime for the new assignment</param>
        /// <param name="asgcontents">The contents of the new assignment</param>
        /// <returns>A JSON object containing success = true/false</returns>
        public IActionResult CreateAssignment(string subject, int num, string season, int year, string category, string asgname, int asgpoints, DateTime asgdue, string asgcontents)
        {
            var acId =
                (from asgcat in db.AssignmentCategories
                 join cl in db.Classes on asgcat.ClassId equals cl.ClassId
                 join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                 where cr.Subject == subject &&
                     cr.Number == num &&
                     cl.Semester == season &&
                     cl.Year == year &&
                     asgcat.Name == category
                 select asgcat.AcId).FirstOrDefault();

            uint? check = acId;

            if (check != null)
            {
                bool nameNotUnique =
                    (from asg in db.Assignments
                     where asg.AcId == acId &&
                        asg.Name == asgname
                     select asg).Any();

                if (nameNotUnique)
                {
                    return Json(new { success = false });
                }
                else
                {
                    var newAsg = new Assignment()
                    {
                        AcId = acId,
                        Name = asgname,
                        MaxPoints = (uint)asgpoints,
                        Contents = asgcontents,
                        DueDate = asgdue
                    };
                    db.Assignments.Add(newAsg);
                    int changedRows = db.SaveChanges();

                    var students =
                        (from g in db.Grades
                        join cl in db.Classes on g.ClassId equals cl.ClassId
                        join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                        where cr.Subject == subject &&
                            cr.Number == num &&
                            cl.Semester == season &&
                            cl.Year == year
                        select g).ToArray();

                    foreach (var s in students)
                    {
                        bool updatedGrade = UpdateGrade(subject, num, season, year, s.UId);
                    }

                    
                    return Json(new { success = (changedRows != 0) });
                }

            }
            else
            {
                return Json(new { success = false });
            }

        }


        /// <summary>
        /// Gets a JSON array of all the submissions to a certain assignment.
        /// Each object in the array should have the following fields:
        /// "fname" - first name
        /// "lname" - last name
        /// "uid" - user ID
        /// "time" - DateTime of the submission
        /// "score" - The score given to the submission
        /// 
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetSubmissionsToAssignment(string subject, int num, string season, int year, string category, string asgname)
        {
            var query =
                from s in db.Submissions
                join asg in db.Assignments on s.AId equals asg.AId
                join asgcat in db.AssignmentCategories on asg.AcId equals asgcat.AcId
                join cl in db.Classes on asgcat.ClassId equals cl.ClassId
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                join st in db.Students on s.Student equals st.UId
                where cr.Subject == subject &&
                    cr.Number == num &&
                    cl.Semester == season &&
                    cl.Year == year &&
                    asgcat.Name == category &&
                    asg.Name == asgname
                select new
                {
                    fname = st.FirstName,
                    lname = st.LastName,
                    uid = st.UId,
                    time = s.SubmitTime,
                    score = s.Score
                };

            return Json(query.ToArray());
        }


        /// <summary>
        /// Set the score of an assignment submission
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment</param>
        /// <param name="uid">The uid of the student who's submission is being graded</param>
        /// <param name="score">The new score for the submission</param>
        /// <returns>A JSON object containing success = true/false</returns>
        public IActionResult GradeSubmission(string subject, int num, string season, int year, string category, string asgname, string uid, int score)
        {
            var query =
                (from s in db.Submissions
                join asg in db.Assignments on s.AId equals asg.AId
                join asgcat in db.AssignmentCategories on asg.AcId equals asgcat.AcId
                join cl in db.Classes on asgcat.ClassId equals cl.ClassId
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                where cr.Subject == subject &&
                    cr.Number == num &&
                    cl.Semester == season &&
                    cl.Year == year &&
                    asgcat.Name == category &&
                    asg.Name == asgname &&
                    s.Student == uid
                select s).FirstOrDefault();

            if (query != null)
            {
                query.Score = score;
                int changedRows = db.SaveChanges();

                bool updatedGrade = UpdateGrade(subject, num, season, year, uid);

                return Json(new { success = (changedRows != 0) });
            }

            return Json(new { success = false });
        }


        /// <summary>
        /// Returns a JSON array of the classes taught by the specified professor
        /// Each object in the array should have the following fields:
        /// "subject" - The subject abbreviation of the class (such as "CS")
        /// "number" - The course number (such as 5530)
        /// "name" - The course name
        /// "season" - The season part of the semester in which the class is taught
        /// "year" - The year part of the semester in which the class is taught
        /// </summary>
        /// <param name="uid">The professor's uid</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetMyClasses(string uid)
        {
            var query =
                from cl in db.Classes
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                where cl.Professor == uid
                select new
                {
                    subject = cr.Subject,
                    number = cr.Number,
                    name = cr.Name,
                    season = cl.Semester,
                    year = cl.Year
                };
            return Json(query.ToArray());
        }

        private bool UpdateGrade(string subject, int num, string season, int year, string uid)
        {
            //Console.WriteLine("\n\n\n\nSTART UPDATE GRADE\n\n\n\n");

            string grade = "--";
            //Console.WriteLine("Grade: " + grade);

            var query =
                (from asgcat in db.AssignmentCategories
                join cl in db.Classes on asgcat.ClassId equals cl.ClassId
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                where cr.Subject == subject &&
                    cr.Number == num &&
                    cl.Semester == season &&
                    cl.Year == year
                select new
                {
                    gw = asgcat.GradingWeight,
                    asgScores = (from asg in db.Assignments
                                where asg.AcId == asgcat.AcId
                                select new
                                {
                                    maxPoints = asg.MaxPoints,
                                    points = (from s in db.Submissions
                                              where s.Student == uid &&
                                                    asg.AId == s.AId
                                              select s.Score).FirstOrDefault()
                                }).ToArray()
                }).ToArray();


            float totalScaledTotals = 0;
            float totalGradingWeights = 0;

            

            foreach (var ac in query)
            {

                if (ac.asgScores.Length == 0)
                {
                    //Console.WriteLine("\n\n\n\nEMPTY ASSIGNMENT CATEGORY\n\n\n\n");
                    continue;
                }

                int gw = (int)ac.gw;

                float totalPoints = 0;
                int totalMaxPoints = 0;

                foreach (var asg in ac.asgScores)
                {
                    totalMaxPoints += (int)asg.maxPoints;

                    float? temp = asg.points;
                    float points = temp == null ? 0 : asg.points;

                    totalPoints += points;

                    //Console.WriteLine("Points " + points);
                }

                float acPercent = totalPoints / totalMaxPoints;
                float scaledTotal = gw * acPercent;

                //Console.Write("\nGW: " + gw);
                //Console.Write("\tTotal Points: " + totalPoints);
                //Console.Write("\tTotal Max Points: " + totalMaxPoints);
                //Console.Write("\tAsg Cat Percent: " + acPercent);
                //Console.Write("\tScaled Total: " + scaledTotal + "\n");

                totalScaledTotals += scaledTotal;
                totalGradingWeights += gw;

            }

            float scalingFactor = (float)100 / (float)totalGradingWeights;
            float totalPercentage = totalScaledTotals * scalingFactor;

            //Console.Write("\n\nTotal Scaled Totals: " + totalScaledTotals);
            //Console.Write("\tTotal Grading Weights: " + totalGradingWeights);
            //Console.Write("\tScaling Factor: " + scalingFactor);
            //Console.Write("\tTotal Percentage: " + totalPercentage + "\n");

            //Console.WriteLine("\n\n\n\nPERCENTAGE\n\n\n\n");
            //Console.WriteLine("totalprecentage: " + totalPercentage);
            //Console.WriteLine("\n\n\n\nPERCENTAGE\n\n\n\n");

            grade = totalPercentage switch
            {
                <= 100 and >= 92 => "A",
                < 92 and >= 90 => "A-",
                < 90 and >= 87 => "B+",
                < 87 and >= 82 => "B",
                < 82 and >= 80 => "B-",
                < 80 and >= 77 => "C+",     // grade >= 92 = A, >= 90 = A-, >= 87 = B+, >= 82 = B, >= 80 = B-, etc. Any grade lower than a 60 is an E.
                < 77 and >= 72 => "C",
                < 72 and >= 70 => "C-",
                < 70 and >= 67 => "D+",
                < 67 and >= 62 => "D",
                < 62 and >= 60 => "D-",
                < 60 and >= 0 => "E",
                > 100 => "A+",
                < 0 => "Er",
                _ => "Er"
            };

            //Console.WriteLine("\n\nGrade After Switch: " + grade);

            if (grade != "--" && grade != "Er")
            {
                var grade_row =
                    (from g in db.Grades
                     join cl in db.Classes on g.ClassId equals cl.ClassId
                     join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                     where cr.Subject == subject &&
                         cr.Number == num &&
                         cl.Semester == season &&
                         cl.Year == year &&
                         g.UId == uid
                     select g).FirstOrDefault();

                if (grade_row != null)
                {
                    grade_row.Grade1 = grade;
                    int changedRows = db.SaveChanges();
                    if (changedRows == 0)
                    {
                        grade = "Er";
                    }
                }
            }

            return (grade != "--" && grade != "Er");
            
        }

        /*******End code to modify********/
    }
}


using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS.Controllers
{
    public class CommonController : Controller
    {
        //If your context class is named differently, fix this
        //and the constructor parameter
        private readonly LMSContext db;

        public CommonController(LMSContext _db)
        {
            db = _db;
        }

        /*******Begin code to modify********/

        /// <summary>
        /// Retreive a JSON array of all departments from the database.
        /// Each object in the array should have a field called "name" and "subject",
        /// where "name" is the department name and "subject" is the subject abbreviation.
        /// </summary>
        /// <returns>The JSON array</returns>
        public IActionResult GetDepartments()
        {

            var query =
                from d in db.Departments
                select new { subject = d.Subject, name = d.Name };

            //JsonSerializer.Serialize(query);

            return Json(query.ToArray());
        }



        /// <summary>
        /// Returns a JSON array representing the course catalog.
        /// Each object in the array should have the following fields:
        /// "subject": The subject abbreviation, (e.g. "CS")
        /// "dname": The department name, as in "Computer Science"
        /// "courses": An array of JSON objects representing the courses in the department.
        ///            Each field in this inner-array should have the following fields:
        ///            "number": The course number (e.g. 5530)
        ///            "cname": The course name (e.g. "Database Systems")
        /// </summary>
        /// <returns>The JSON array</returns>
        public IActionResult GetCatalog()
        {

            var query =
                from d in db.Departments
                select new
                {
                    subject = d.Subject, dname = d.Name,
                    courses = 
                        (from c in db.Courses
                        where d.Subject == c.Subject
                        select new
                        {
                            number = c.Number, cname = c.Name
                        }).ToArray()
                };

            return Json(query.ToArray());
        }

        /// <summary>
        /// Returns a JSON array of all class offerings of a specific course.
        /// Each object in the array should have the following fields:
        /// "season": the season part of the semester, such as "Fall"
        /// "year": the year part of the semester
        /// "location": the location of the class
        /// "start": the start time in format "hh:mm:ss"
        /// "end": the end time in format "hh:mm:ss"
        /// "fname": the first name of the professor
        /// "lname": the last name of the professor
        /// </summary>
        /// <param name="subject">The subject abbreviation, as in "CS"</param>
        /// <param name="number">The course number, as in 5530</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetClassOfferings(string subject, int number)
        {
            //Console.WriteLine("\n\n\n\nStarted GetClassOfferings\n\n\n\n");

            var query =
                from cl in db.Classes
                join p in db.Professors on cl.Professor equals p.UId
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                where subject == cr.Subject &&
                    number == cr.Number
                select new {
                    season = cl.Semester,
                    year = cl.Year,
                    location = cl.Location,
                    start = cl.Start.ToString(),
                    end = cl.End.ToString(),
                    fname = p.FirstName,
                    lname = p.LastName
                };

            //JsonSerializer.Serialize(query.ToArray());

            //Console.WriteLine("\n\n\n\nPrinting\n\n\n\n");
            //Console.WriteLine(query);
            //Console.WriteLine("\n\n\n\nDone Printing\n\n\n\n");


            return Json(query.ToArray());
        }

        /// <summary>
        /// This method does NOT return JSON. It returns plain text (containing html).
        /// Use "return Content(...)" to return plain text.
        /// Returns the contents of an assignment.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment in the category</param>
        /// <returns>The assignment contents</returns>
        public IActionResult GetAssignmentContents(string subject, int num, string season, int year, string category, string asgname)
        {
            var query =
                (from asg in db.Assignments
                join asgcat in db.AssignmentCategories on asg.AcId equals asgcat.AcId
                join cl in db.Classes on asgcat.ClassId equals cl.ClassId
                join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                where subject == cr.Subject &&
                    num == cr.Number &&
                    season == cl.Semester &&
                    year == cl.Year &&
                    category == asgcat.Name &&
                    asgname == asg.Name
                select asg.Contents).FirstOrDefault();

            string ret = "";
            if (query != null)
            {
                ret = query;
            }


            return Content(ret);
        }


        /// <summary>
        /// This method does NOT return JSON. It returns plain text (containing html).
        /// Use "return Content(...)" to return plain text.
        /// Returns the contents of an assignment submission.
        /// Returns the empty string ("") if there is no submission.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment in the category</param>
        /// <param name="uid">The uid of the student who submitted it</param>
        /// <returns>The submission text</returns>
        public IActionResult GetSubmissionText(string subject, int num, string season, int year, string category, string asgname, string uid)
        {
            var query =
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
                 select s.Contents).FirstOrDefault();

            string ret = "";
            if (query != null)
            {
                ret = query;
            }
                

            return Content(ret);
        }


        /// <summary>
        /// Gets information about a user as a single JSON object.
        /// The object should have the following fields:
        /// "fname": the user's first name
        /// "lname": the user's last name
        /// "uid": the user's uid
        /// "department": (professors and students only) the name (such as "Computer Science") of the department for the user. 
        ///               If the user is a Professor, this is the department they work in.
        ///               If the user is a Student, this is the department they major in.    
        ///               If the user is an Administrator, this field is not present in the returned JSON
        /// </summary>
        /// <param name="uid">The ID of the user</param>
        /// <returns>
        /// The user JSON object 
        /// or an object containing {success: false} if the user doesn't exist
        /// </returns>
        public IActionResult GetUser(string uid)
        {
            var studentID =
                (from s in db.Students
                where uid == s.UId
                select new
                {
                    fname = s.FirstName,
                    lname = s.LastName,
                    uid = s.UId,
                    department = (from d in db.Departments
                                 where s.Subject == d.Subject
                                 select d.Name).FirstOrDefault()
                }).FirstOrDefault();

            var profID =
                (from p in db.Professors
                 where uid == p.UId
                 select new
                 {
                     fname = p.FirstName,
                     lname = p.LastName,
                     uid = p.UId,
                     department = (from d in db.Departments
                                   where p.Subject == d.Subject
                                   select d.Name).FirstOrDefault()
                 }).FirstOrDefault();

            var adminID =
                (from a in db.Administrators
                 where uid == a.UId
                 select new
                 {
                     fname = a.FirstName,
                     lname = a.LastName,
                     uid = a.UId
                 }).FirstOrDefault();

            if (studentID != null)
            {
                return Json(studentID);
            }
            else if (profID != null)
            {
                return Json(profID);
            }
            else if (adminID != null)
            {
                return Json(adminID);
            }
            else
            {
                return Json(new { success = false });
            }
            
        }


        /*******End code to modify********/
    }
}


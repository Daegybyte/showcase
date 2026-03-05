using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Mvc;
using static System.Net.Mime.MediaTypeNames;

using System.Globalization;
using System.Xml.Linq;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS.Controllers
{
    public class AdministratorController : Controller
    {

        //If your context class is named something different,
        //fix this member var and the constructor param
        private readonly LMSContext db;

        public AdministratorController(LMSContext _db)
        {
            db = _db;
        }

        // GET: /<controller>/
        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Department(string subject)
        {
            ViewData["subject"] = subject;
            return View();
        }

        public IActionResult Course(string subject, string num)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            return View();
        }

        /*******Begin code to modify********/

        /// <summary>
        /// Create a department which is uniquely identified by it's subject code
        /// </summary>
        /// <param name="subject">the subject code</param>
        /// <param name="name">the full name of the department</param>
        /// <returns>A JSON object containing {success = true/false}.
        /// false if the department already exists, true otherwise.</returns>
        public IActionResult CreateDepartment(string subject, string name)
        {
            int changedRows = 0;

            subject = subject.ToUpper();

            //name = Regex.Replace(name, "^[a-z]", m => m.Value.ToUpper());
            TextInfo textinfo = new CultureInfo("en-US", false).TextInfo;
            name = textinfo.ToTitleCase(name);

            var alreadyDept =
                (from d in db.Departments
                 where d.Subject == subject
                 select d).Any();

            if (!alreadyDept)
            {
                var newDept = new Department()
                {
                    Subject = subject,
                    Name = name
                };
                db.Departments.Add(newDept);
                changedRows = db.SaveChanges();
            }

            return Json(new { success = (changedRows != 0) });
        }


        /// <summary>
        /// Returns a JSON array of all the courses in the given department.
        /// Each object in the array should have the following fields:
        /// "number" - The course number (as in 5530)
        /// "name" - The course name (as in "Database Systems")
        /// </summary>
        /// <param name="subjCode">The department subject abbreviation (as in "CS")</param>
        /// <returns>The JSON result</returns>
        public IActionResult GetCourses(string subject)
        {
            var query =
                from c in db.Courses
                where c.Subject == subject
                select new { number = c.Number, name = c.Name };

            return Json(query);
        }

        /// <summary>
        /// Returns a JSON array of all the professors working in a given department.
        /// Each object in the array should have the following fields:
        /// "lname" - The professor's last name
        /// "fname" - The professor's first name
        /// "uid" - The professor's uid
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <returns>The JSON result</returns>
        public IActionResult GetProfessors(string subject)
        {
            var query =
                from p in db.Professors
                where p.Subject == subject
                select new { lname = p.LastName, fname = p.FirstName, uid = p.UId };

            return Json(query);
            
        }



        /// <summary>
        /// Creates a course.
        /// A course is uniquely identified by its number + the subject to which it belongs
        /// </summary>
        /// <param name="subject">The subject abbreviation for the department in which the course will be added</param>
        /// <param name="number">The course number</param>
        /// <param name="name">The course name</param>
        /// <returns>A JSON object containing {success = true/false}.
        /// false if the course already exists, true otherwise.</returns>
        public IActionResult CreateCourse(string subject, int number, string name)
        {
            int changedRows = 0;

            TextInfo textinfo = new CultureInfo("en-US", false).TextInfo;
            name = textinfo.ToTitleCase(name);

            var alreadyCourse =
                (from c in db.Courses
                 where c.Subject == subject && c.Number == number
                 select c).Any();

            if (!alreadyCourse)
            {
                var newCourse = new Course()
                {
                    Name = name,
                    Subject = subject,
                    Number = number
                };
                db.Courses.Add(newCourse);
                changedRows = db.SaveChanges();
            }

            return Json(new { success = (changedRows != 0) });
        }



        /// <summary>
        /// Creates a class offering of a given course.
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <param name="number">The course number</param>
        /// <param name="season">The season part of the semester</param>
        /// <param name="year">The year part of the semester</param>
        /// <param name="start">The start time</param>
        /// <param name="end">The end time</param>
        /// <param name="location">The location</param>
        /// <param name="instructor">The uid of the professor</param>
        /// <returns>A JSON object containing {success = true/false}. 
        /// false if another class occupies the same location during any time 
        /// within the start-end range in the same semester, or if there is already
        /// a Class offering of the same Course in the same Semester,
        /// true otherwise.</returns>
        public IActionResult CreateClass(string subject, int number, string season, int year, DateTime start, DateTime end, string location, string instructor)
        {
            TextInfo textinfo = new CultureInfo("en-US", false).TextInfo;
            location = textinfo.ToTitleCase(location);

            bool conflict =
                (from cl in db.Classes
                 join cr in db.Courses on cl.CatalogId equals cr.CatalogId
                 where
                    (cr.Subject == subject &&           // duplicate class in same semester and year
                        cr.Number == number &&
                        cl.Year == year &&
                        cl.Semester == season
                     )   
                    ||
                    (cl.Year == year &&
                        cl.Semester == season &&
                        cl.Location == location &&                           // location and time conflict
                        ((cl.Start.IsBetween(TimeOnly.FromDateTime(start), TimeOnly.FromDateTime(end)))
                        ||
                        (TimeOnly.FromDateTime(start).IsBetween(cl.Start, cl.End))))

                 select cl).Any();

            if (!conflict)
            {
                var catalogID =
                    (from c in db.Courses
                    where c.Subject == subject && c.Number == number
                    select c.CatalogId).FirstOrDefault();

                var newClass = new Class()
                {
                    CatalogId = catalogID,
                    Semester = season,
                    Year = (uint)year,
                    Professor = instructor,
                    Location = location,
                    Start = TimeOnly.FromDateTime(start),
                    End = TimeOnly.FromDateTime(end)
                };
                db.Classes.Add(newClass);
                int changedRows = db.SaveChanges();
                return Json(new { success = (changedRows != 0) });
            }
            else
            {
                return Json(new { success = false });
            }
            
        }


        /*******End code to modify********/

    }
}


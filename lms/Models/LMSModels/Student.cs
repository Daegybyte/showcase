using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Student
    {
        public Student()
        {
            Grades = new HashSet<Grade>();
            Submissions = new HashSet<Submission>();
        }

        public string UId { get; set; } = null!;
        public string FirstName { get; set; } = null!;
        public string LastName { get; set; } = null!;
        public DateOnly Dob { get; set; }
        public string Subject { get; set; } = null!;

        public virtual Department SubjectNavigation { get; set; } = null!;
        public virtual ICollection<Grade> Grades { get; set; }
        public virtual ICollection<Submission> Submissions { get; set; }
    }
}

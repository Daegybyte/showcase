using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Class
    {
        public Class()
        {
            AssignmentCategories = new HashSet<AssignmentCategory>();
            Grades = new HashSet<Grade>();
        }

        public uint ClassId { get; set; }
        public uint CatalogId { get; set; }
        public string Semester { get; set; } = null!;
        public uint Year { get; set; }
        public string Professor { get; set; } = null!;
        public string Location { get; set; } = null!;
        public TimeOnly Start { get; set; }
        public TimeOnly End { get; set; }

        public virtual Course Catalog { get; set; } = null!;
        public virtual Professor ProfessorNavigation { get; set; } = null!;
        public virtual ICollection<AssignmentCategory> AssignmentCategories { get; set; }
        public virtual ICollection<Grade> Grades { get; set; }
    }
}

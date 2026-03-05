using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Course
    {
        public Course()
        {
            Classes = new HashSet<Class>();
        }

        public uint CatalogId { get; set; }
        public string Name { get; set; } = null!;
        public string Subject { get; set; } = null!;
        public int Number { get; set; }

        public virtual Department SubjectNavigation { get; set; } = null!;
        public virtual ICollection<Class> Classes { get; set; }
    }
}

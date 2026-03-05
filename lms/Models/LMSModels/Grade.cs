using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Grade
    {
        public uint ClassId { get; set; }
        public string UId { get; set; } = null!;
        public string Grade1 { get; set; } = null!;

        public virtual Class Class { get; set; } = null!;
        public virtual Student UIdNavigation { get; set; } = null!;
    }
}

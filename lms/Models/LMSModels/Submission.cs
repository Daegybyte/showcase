using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Submission
    {
        public uint SId { get; set; }
        public uint AId { get; set; }
        public string Student { get; set; } = null!;
        public DateTime SubmitTime { get; set; }
        public string Contents { get; set; } = null!;
        public float Score { get; set; }

        public virtual Assignment AIdNavigation { get; set; } = null!;
        public virtual Student StudentNavigation { get; set; } = null!;
    }
}

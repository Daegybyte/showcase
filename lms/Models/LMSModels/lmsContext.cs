using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;

namespace LMS.Models.LMSModels
{
    public partial class LMSContext : DbContext
    {
        public LMSContext()
        {
        }

        public LMSContext(DbContextOptions<LMSContext> options)
            : base(options)
        {
        }

        public virtual DbSet<Administrator> Administrators { get; set; } = null!;
        public virtual DbSet<Assignment> Assignments { get; set; } = null!;
        public virtual DbSet<AssignmentCategory> AssignmentCategories { get; set; } = null!;
        public virtual DbSet<Class> Classes { get; set; } = null!;
        public virtual DbSet<Course> Courses { get; set; } = null!;
        public virtual DbSet<Department> Departments { get; set; } = null!;
        public virtual DbSet<Grade> Grades { get; set; } = null!;
        public virtual DbSet<Professor> Professors { get; set; } = null!;
        public virtual DbSet<Student> Students { get; set; } = null!;
        public virtual DbSet<Submission> Submissions { get; set; } = null!;

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
                optionsBuilder.UseMySql("name=LMS:LMSConnectionString", Microsoft.EntityFrameworkCore.ServerVersion.Parse("10.1.48-mariadb"));
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.UseCollation("latin1_swedish_ci")
                .HasCharSet("latin1");

            modelBuilder.Entity<Administrator>(entity =>
            {
                entity.HasKey(e => e.UId)
                    .HasName("PRIMARY");

                entity.Property(e => e.UId)
                    .HasMaxLength(8)
                    .HasColumnName("uID");

                entity.Property(e => e.Dob).HasColumnName("DOB");

                entity.Property(e => e.FirstName).HasMaxLength(100);

                entity.Property(e => e.LastName).HasMaxLength(100);
            });

            modelBuilder.Entity<Assignment>(entity =>
            {
                entity.HasKey(e => e.AId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.AcId, "Assignments_ibfk_1");

                entity.Property(e => e.AId)
                    .HasColumnType("int(10) unsigned")
                    .HasColumnName("aID");

                entity.Property(e => e.AcId)
                    .HasColumnType("int(10) unsigned")
                    .HasColumnName("acID");

                entity.Property(e => e.Contents).HasMaxLength(8192);

                entity.Property(e => e.DueDate).HasColumnType("datetime");

                entity.Property(e => e.MaxPoints).HasColumnType("int(10) unsigned");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.HasOne(d => d.Ac)
                    .WithMany(p => p.Assignments)
                    .HasForeignKey(d => d.AcId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Assignments_ibfk_1");
            });

            modelBuilder.Entity<AssignmentCategory>(entity =>
            {
                entity.HasKey(e => e.AcId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.ClassId, "ClassID");

                entity.HasIndex(e => new { e.Name, e.ClassId }, "UC_Category")
                    .IsUnique();

                entity.Property(e => e.AcId)
                    .HasColumnType("int(10) unsigned")
                    .HasColumnName("acID");

                entity.Property(e => e.ClassId)
                    .HasColumnType("int(10) unsigned")
                    .HasColumnName("ClassID");

                entity.Property(e => e.GradingWeight).HasColumnType("int(3) unsigned");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.HasOne(d => d.Class)
                    .WithMany(p => p.AssignmentCategories)
                    .HasForeignKey(d => d.ClassId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("AssignmentCategories_ibfk_1");
            });

            modelBuilder.Entity<Class>(entity =>
            {
                entity.HasIndex(e => e.Professor, "Classes_ibfk_2");

                entity.HasIndex(e => new { e.CatalogId, e.Semester, e.Year }, "UC_Class")
                    .IsUnique();

                entity.Property(e => e.ClassId)
                    .HasColumnType("int(10) unsigned")
                    .HasColumnName("ClassID");

                entity.Property(e => e.CatalogId)
                    .HasColumnType("int(5) unsigned")
                    .HasColumnName("CatalogID");

                entity.Property(e => e.End).HasColumnType("time");

                entity.Property(e => e.Location).HasMaxLength(100);

                entity.Property(e => e.Professor).HasMaxLength(8);

                entity.Property(e => e.Semester).HasMaxLength(6);

                entity.Property(e => e.Start).HasColumnType("time");

                entity.Property(e => e.Year).HasColumnType("int(4) unsigned");

                entity.HasOne(d => d.Catalog)
                    .WithMany(p => p.Classes)
                    .HasForeignKey(d => d.CatalogId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Classes_ibfk_3");

                entity.HasOne(d => d.ProfessorNavigation)
                    .WithMany(p => p.Classes)
                    .HasForeignKey(d => d.Professor)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Classes_ibfk_2");
            });

            modelBuilder.Entity<Course>(entity =>
            {
                entity.HasKey(e => e.CatalogId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.Subject, "Subject");

                entity.Property(e => e.CatalogId)
                    .HasColumnType("int(5) unsigned")
                    .HasColumnName("CatalogID");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.Property(e => e.Number).HasColumnType("int(4)");

                entity.Property(e => e.Subject).HasMaxLength(4);

                entity.HasOne(d => d.SubjectNavigation)
                    .WithMany(p => p.Courses)
                    .HasForeignKey(d => d.Subject)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Courses_ibfk_1");
            });

            modelBuilder.Entity<Department>(entity =>
            {
                entity.HasKey(e => e.Subject)
                    .HasName("PRIMARY");

                entity.Property(e => e.Subject).HasMaxLength(4);

                entity.Property(e => e.Name).HasMaxLength(100);
            });

            modelBuilder.Entity<Grade>(entity =>
            {
                entity.HasKey(e => new { e.ClassId, e.UId })
                    .HasName("PRIMARY")
                    .HasAnnotation("MySql:IndexPrefixLength", new[] { 0, 0 });

                entity.HasIndex(e => e.UId, "uID");

                entity.Property(e => e.ClassId)
                    .HasColumnType("int(10) unsigned")
                    .HasColumnName("ClassID");

                entity.Property(e => e.UId)
                    .HasMaxLength(8)
                    .HasColumnName("uID");

                entity.Property(e => e.Grade1)
                    .HasMaxLength(2)
                    .HasColumnName("Grade");

                entity.HasOne(d => d.Class)
                    .WithMany(p => p.Grades)
                    .HasForeignKey(d => d.ClassId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Grades_ibfk_1");

                entity.HasOne(d => d.UIdNavigation)
                    .WithMany(p => p.Grades)
                    .HasForeignKey(d => d.UId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Grades_ibfk_2");
            });

            modelBuilder.Entity<Professor>(entity =>
            {
                entity.HasKey(e => e.UId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.Subject, "Subject");

                entity.Property(e => e.UId)
                    .HasMaxLength(8)
                    .HasColumnName("uID");

                entity.Property(e => e.Dob).HasColumnName("DOB");

                entity.Property(e => e.FirstName).HasMaxLength(100);

                entity.Property(e => e.LastName).HasMaxLength(100);

                entity.Property(e => e.Subject).HasMaxLength(4);

                entity.HasOne(d => d.SubjectNavigation)
                    .WithMany(p => p.Professors)
                    .HasForeignKey(d => d.Subject)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Professors_ibfk_1");
            });

            modelBuilder.Entity<Student>(entity =>
            {
                entity.HasKey(e => e.UId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.Subject, "Subject");

                entity.Property(e => e.UId)
                    .HasMaxLength(8)
                    .HasColumnName("uID");

                entity.Property(e => e.Dob).HasColumnName("DOB");

                entity.Property(e => e.FirstName).HasMaxLength(100);

                entity.Property(e => e.LastName).HasMaxLength(100);

                entity.Property(e => e.Subject).HasMaxLength(4);

                entity.HasOne(d => d.SubjectNavigation)
                    .WithMany(p => p.Students)
                    .HasForeignKey(d => d.Subject)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Students_ibfk_1");
            });

            modelBuilder.Entity<Submission>(entity =>
            {
                entity.HasKey(e => e.SId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.Student, "Student");

                entity.HasIndex(e => new { e.AId, e.Student, e.SubmitTime }, "UC_Submission")
                    .IsUnique();

                entity.Property(e => e.SId)
                    .HasColumnType("int(10) unsigned")
                    .HasColumnName("sID");

                entity.Property(e => e.AId)
                    .HasColumnType("int(10) unsigned")
                    .HasColumnName("aID");

                entity.Property(e => e.Contents).HasMaxLength(8192);

                entity.Property(e => e.Student).HasMaxLength(8);

                entity.Property(e => e.SubmitTime)
                    .HasColumnType("timestamp")
                    .ValueGeneratedOnAddOrUpdate()
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasOne(d => d.AIdNavigation)
                    .WithMany(p => p.Submissions)
                    .HasForeignKey(d => d.AId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Submissions_ibfk_1");

                entity.HasOne(d => d.StudentNavigation)
                    .WithMany(p => p.Submissions)
                    .HasForeignKey(d => d.Student)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Submissions_ibfk_2");
            });

            OnModelCreatingPartial(modelBuilder);
        }

        partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
    }
}

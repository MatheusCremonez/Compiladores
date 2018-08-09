using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;

namespace VirtualMachine
{
    public partial class InterfaceVM : Form
    {
        public InterfaceVM()
        {
            InitializeComponent();
        }

        private void arquivoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Stream verificator;
            OpenFileDialog file = new OpenFileDialog();
         
            if(file.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                if ((verificator = file.OpenFile()) != null)
                {
                    string fileName = file.FileName;
                    string fileText = File.ReadAllText(fileName);
                    richTextBox1.Text = fileText;
                }
            }
        }

        private void label1_Click(object sender, EventArgs e)
        {

        }

        private void richTextBox1_TextChanged(object sender, EventArgs e)
        {
        
        }

        private void label2_Click(object sender, EventArgs e)
        {

        }

        private void label4_Click(object sender, EventArgs e)
        {

        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        private void label7_Click(object sender, EventArgs e)
        {

        }
    }
}

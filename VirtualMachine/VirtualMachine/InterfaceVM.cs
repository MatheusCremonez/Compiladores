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
        string[] values;
        public InterfaceVM()
        {
            InitializeComponent();
        }

        private void arquivoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            int i, j = 1;

            OpenFileDialog openFile = new OpenFileDialog();

            if (openFile.ShowDialog() == DialogResult.OK)
            {
                StreamReader file = new StreamReader(openFile.FileName);

                DataTable dt = new DataTable();

                dt.Columns.Add("I");
                dt.Columns.Add("Instrução");
                dt.Columns.Add("Atributo #1");
                dt.Columns.Add("Atributo #2");

                string newline;

                while ((newline = file.ReadLine()) != null)
                {
                    DataRow dr = dt.NewRow();
                    values = newline.Split(' ');

                    
                    for (i = 0; i < values.Length; i++)
                    {
                        dr[0] = j;
                        dr[i+1] = values[i];
                    }
                    dt.Rows.Add(dr);
                    j++;
                }
                file.Close();
                dataGridView1.DataSource = dt;
            }

        }

        private void label1_Click(object sender, EventArgs e)
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

        private void startToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Seleciona a linha toda
            //dataGridView1.Rows[2].Selected = true;

            int j = 0;
            
            /*while (j < values.Length)
            {
                MessageBox.Show(dataGridView1.Rows[0].Cells[j].Value.ToString());
                j++;
            }*/

        }

    }
}

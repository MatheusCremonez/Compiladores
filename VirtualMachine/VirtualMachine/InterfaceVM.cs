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

        //Carrega o Arquivo e adiciona ele no dataGrid
        private void arquivoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Variaveis Gerais para esta etapa
            int i, linhaNumero = 1;
            string newline;

            //Pega o arquivo
            OpenFileDialog openFile = new OpenFileDialog();

            //Verifica se o arquivo foi selecionado corretamente
            if (openFile.ShowDialog() == DialogResult.OK)
            {
                //Coloca o arquivo em uma variavel
                StreamReader file = new StreamReader(openFile.FileName);

                //Cria a dataTable para adicionarmos os valores na lista
                DataTable dt = new DataTable();

                //Colunas da tabela
                dt.Columns.Add("I");
                dt.Columns.Add("Instrução");
                dt.Columns.Add("Atributo #1");
                dt.Columns.Add("Atributo #2");


                //Enquanto a tiver linha no arquivo le
                while ((newline = file.ReadLine()) != null)
                {
                    //Cria uma linha para a tabela
                    DataRow dr = dt.NewRow();

                    //Vetor para pegar a linha, separando por espaço
                    string[] values;
                    values = newline.Split(' ');
                    
                    //Adiciona os valores separados por espaço nas colunas
                    for (i = 0; i < values.Length; i++)
                    {
                        dr[0] = linhaNumero;
                        dr[i+1] = values[i];
                    }
                    //Adiciona a linha na tabela
                    dt.Rows.Add(dr);
                    linhaNumero++;
                }

                file.Close();

                //Adiciona a tabela ano dataGrid
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
            //Aqui irá ocorrer a execução de uma vez
        }

        //VALE RESSALTAR QUE AINDA NAO ESTA FAZENDO LINHA APOS LINHA POIS ESTOU CORRIGINDO OS ERROS ANTES DE FICAR TRAVANDO LINHA A LINHA
        //Executa o código linha por linha carregado
        private void startStepToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Variaveis Gerais
            int i = 0, j = 1, endereco = 0;

            while (i < dataGridView1.Rows.Count)
            {
                //Debug
                //MessageBox.Show(dataGridView1.Rows[i].Cells[j].Value.ToString());

                //Funcionando, acredito que seja isso que tem que fazer
                //LDV -- Carrega um valor
                if (dataGridView1.Rows[i].Cells[j].Value.ToString() == "LDV")
                {
                    //Adiciona na listView, o interessante é que o primeiro item (primeira Coluna), é um item e tem que ser passado na criação da linha
                    //e o segundo item (Segunda Coluna) em diante são SubItems
                    ListViewItem item = new ListViewItem(endereco.ToString());
                    item.SubItems.Add(dataGridView1.Rows[i].Cells[j + 1].Value.ToString());
                    listView1.Items.Add(item);
                    endereco++;
                }
                
                //Ainda não está funcionando este código abaixo, não sei porque mas não consigo pegar a linha para poder pegar os valores que tem nela
                if (dataGridView1.Rows[i].Cells[j].Value.ToString() == "ADD")
                {
                    //Debug
                    MessageBox.Show(listView1.SelectedItems.Count.ToString());

                    //Essa verificação é para ver se tem linha selecionada
                    if (listView1.SelectedItems.Count > 0)
                    {
                        //Pega o valor da primeira coluna
                        string valor = listView1.SelectedItems[0].SubItems[0].Text;

                        //Debug
                        MessageBox.Show(valor);
                    }

                    //Após pegar os dois valores, tem que fazer a soma e então adicionar o richTextBox4
                    //richTextBox4.Text = contaFeita;
                }
                i++;
            }
        }

        private void listView1_SelectedIndexChanged(object sender, EventArgs e)
        {

        }
    }
}

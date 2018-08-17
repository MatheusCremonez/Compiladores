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

        private void startToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Aqui irá ocorrer a execução de uma vez
        }

        //VALE RESSALTAR QUE AINDA NAO ESTA FAZENDO LINHA APOS LINHA POIS ESTOU CORRIGINDO OS ERROS ANTES DE FICAR TRAVANDO LINHA A LINHA
        //Executa o código linha por linha carregado
        private void startStepToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Variaveis Gerais
            int linha = 0, atributo = 1;

            while (linha < dataGridView1.Rows.Count)
            {
                Instruction instruction = new Instruction(dataGridView1.Rows[linha].Cells[atributo].Value.ToString(), dataGridView1.Rows[linha].Cells[atributo + 1].Value.ToString(), dataGridView1.Rows[linha].Cells[atributo + 2].Value.ToString());
                linha++;
            }
        }

    }

    public class Instruction
    {
        public String instruction;
        public String firstAttribute;
        public String secondAttribute;

        public Instruction(String instruction, String firstAttribute, String secondAttribute)
        {
            this.instruction = instruction;
            this.firstAttribute = firstAttribute;
            this.secondAttribute = secondAttribute;

            if(String.IsNullOrEmpty(instruction))
            {
                throw new Exception("Instruction not provided");
            } else {
                executeInstruction();
            }

        }

        public void executeInstruction()
        {
            //Nao funciona este objeto, nao sei porque
            InterfaceVM interfaceVM = new InterfaceVM();

            int endereco = 0;
            
            switch (instruction) {
                case "LDV":
                    ListViewItem item = new ListViewItem(endereco.ToString());
                    item.SubItems.Add(firstAttribute);
                    interfaceVM.listView1.Items.Add(item);
                    endereco++;
                    break;

                case "ADD":

                    //Codigo quebrado, ainda precisa arrumar
                    /*//Debug
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
                    //richTextBox4.Text = contaFeita; */
                    break;
                default:
                    break;

            }
        }
    }
}

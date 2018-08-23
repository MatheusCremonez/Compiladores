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
using System.Collections;

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
                //Cria a dataTable para adicionarmos os valores na lista
                DataTable dt = new DataTable();

                //Adiciona a tabela ano dataGrid
                dataGridView1.DataSource = dt;

                //Colunas da tabela
                dt.Columns.Add("I");
                dt.Columns.Add("Instrução");
                dt.Columns.Add("Atributo #1");
                dt.Columns.Add("Atributo #2");

                //Preenche as colunas para caber no espaço do dataGrid
                dataGridView1.Columns[0].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
                dataGridView1.Columns[1].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
                dataGridView1.Columns[2].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
                dataGridView1.Columns[3].AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;

                //Coloca o arquivo em uma variavel
                StreamReader file = new StreamReader(openFile.FileName);

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

                dataGridView1.Rows[0].Selected = true;
                file.Close();

            }

        }

        //Executa o código direto, sem passar linha a linha
        private void startToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Cria a dataTable para adicionarmos colunas e linhas na lista
            DataTable dt = new DataTable();
            //Coloca no dataGrid as colunas criadas
            dataGridView2.DataSource = dt;

            //Colunas da tabela
            dt.Columns.Add("Endereço(s):");
            dt.Columns.Add("Valor:");

            //Preenche as colunas para caber no espaço do dataGrid
            dataGridView2.Columns[0].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
            dataGridView2.Columns[1].AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;

            //Variaveis Gerais
            int linhaInstrucao = 0, atributo = 1, validate = 0;
            int topoDaPilha = 0;

            //Array que guarda os valores da pilha e posição
            ArrayList arrayStash = new ArrayList();
            //Objeto da Classe que contém as instruções que o Compilador faz
            Instruction instruction = new Instruction();

            //Objeto para pegar uma linha do dataGrid
            DataGridViewRow row = new DataGridViewRow();

            while (topoDaPilha != -99)
            {
                dataGridView1.ClearSelection();

                topoDaPilha = instruction.execute(dataGridView1.Rows[linhaInstrucao].Cells[atributo].Value.ToString(), dataGridView1.Rows[linhaInstrucao].Cells[atributo + 1].Value.ToString(), dataGridView1.Rows[linhaInstrucao].Cells[atributo + 2].Value.ToString(), arrayStash, topoDaPilha);
                
                if(!(dataGridView1.Rows[linhaInstrucao].Cells[atributo].Value.ToString().Equals("START")) && !(dataGridView1.Rows[linhaInstrucao].Cells[atributo].Value.ToString().Equals("HLT")))
                {
                    dataGridView2.ClearSelection();

                    for (int i = 1; i < dataGridView2.Rows.Count; i++)
                    {
                        row = dataGridView2.Rows[i];

                        if (topoDaPilha == Convert.ToInt32(row.Cells[0].Value))
                        {
                            row.Cells[0].Value = topoDaPilha;
                            row.Cells[1].Value = arrayStash[topoDaPilha].ToString();
                            validate = 1;
                            dataGridView2.Rows[topoDaPilha].Selected = true;
                        }
                    }

                    if (validate == 0)
                    {
                        dt.Rows.Add(topoDaPilha, arrayStash[topoDaPilha].ToString());
                        dataGridView2.Rows[topoDaPilha].Selected = true;
                    }

                    if (dataGridView1.Rows[linhaInstrucao].Cells[atributo].Value.ToString().Equals("ADD"))
                    {
                        richTextBox4.AppendText(arrayStash[topoDaPilha].ToString() + "\n");
                    }
                }
                
                dataGridView1.Rows[linhaInstrucao].Selected = true;
                linhaInstrucao++;
            }
           
        }
        
        private void startStepToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Executa o código linha por linha carregado
        }
    }

    public class Instruction
    {
        public int execute(string instruction, string firstAttribute, string secondAttribute, ArrayList array, int topoPilha)
        {
            int x, y;
            if (String.IsNullOrEmpty(instruction))
            {
                throw new Exception("Instruction not provided");
            }
            else
            {
                switch (instruction)
                {
                    case "START":
                        topoPilha = -1;
                        return topoPilha;

                    case "LDC":
                        topoPilha++;
                        array.Add(firstAttribute);
                        return topoPilha;

                    case "LDV":
                        topoPilha++;
                        array.Add(array[Convert.ToInt32(firstAttribute)]);
                        return topoPilha;

                    case "ADD":
                        x = Convert.ToInt32(array[topoPilha]);
                        y = Convert.ToInt32(array[topoPilha - 1]);
                        array[topoPilha - 1] = x + y;
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "SUB":
                        x = Convert.ToInt32(array[topoPilha]);
                        y = Convert.ToInt32(array[topoPilha - 1]);
                        array[topoPilha - 1] = x - y;
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "MULT":
                        x = Convert.ToInt32(array[topoPilha]);
                        y = Convert.ToInt32(array[topoPilha - 1]);
                        array[topoPilha - 1] = x * y;
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "DIVI":
                        x = Convert.ToInt32(array[topoPilha]);
                        y = Convert.ToInt32(array[topoPilha - 1]);
                        array[topoPilha - 1] = x / y;
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "INV":
                        x = Convert.ToInt32(array[topoPilha]);
                        array[topoPilha] = x * (-1);
                        return topoPilha;

                    case "HLT":
                        return -99;
                    default:
                        return topoPilha;

                }
            }
        }
    }
}

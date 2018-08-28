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
        //Variáveis Globais Para Uso do Passo a Passo
        int topoDaPilhaStep = -99, linhaInstrucaoStep = 0;
        ArrayList arrayStashStep = new ArrayList();
        Instruction instructionStep = new Instruction();
        //Cria a dataTable para adicionarmos colunas e linhas na lista
        DataTable dtStep = new DataTable();

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
            int linhaInstrucao = 0, i;
            int topoDaPilha = 0;
            string instructionName, firstAttribute, secondAttribute;

            //Array que guarda os valores da pilha e posição
            ArrayList arrayStash = new ArrayList();
            //Objeto da Classe que contém as instruções que o Compilador faz
            Instruction instruction = new Instruction();

            while (topoDaPilha != -99)
            {
                dataGridView1.ClearSelection();

                instructionName = dataGridView1.Rows[linhaInstrucao].Cells[1].Value.ToString();
                firstAttribute = dataGridView1.Rows[linhaInstrucao].Cells[2].Value.ToString();
                secondAttribute = dataGridView1.Rows[linhaInstrucao].Cells[3].Value.ToString();

                if(instructionName.Equals("JMP") || instructionName.Equals("JMPF"))
                {
                    int newInstructionLine = instruction.executeJump(dataGridView1, instructionName, firstAttribute);
                    if(newInstructionLine != (-1))
                    {
                        linhaInstrucao = newInstructionLine;
                    }
                    else
                    {
                        //Não encontrou a linha especificada no jump
                        //Verificar criação de exceções para cada tipo de erro possível na máquina virtual
                    }
                }
                else
                {
                    topoDaPilha = instruction.execute(instructionName, firstAttribute, secondAttribute, arrayStash, topoDaPilha);
                }
                

                if (!(instructionName.Equals("START")) && !(instructionName.Equals("HLT")) && !(instructionName.Equals("JMP")))
                {
                    dt.Clear();

                    for (i = 0; i < arrayStash.Count; i++)
                    {
                        dt.Rows.Add(i, arrayStash[i].ToString());
                    }

                    dataGridView2.ClearSelection();
                    dataGridView2.Rows[topoDaPilha].Selected = true;

                    if (instructionName.Equals("PRN"))
                    {
                        richTextBox4.AppendText(arrayStash[topoDaPilha].ToString() + "\n");
                        topoDaPilha--;
                    }
                }

                dataGridView1.Rows[linhaInstrucao].Selected = true;
                linhaInstrucao++;
            }

        }
        
        private void startStepToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Coloca no dataGrid as colunas criadas
            dataGridView2.DataSource = dtStep;

            //Verificação para caso queira executar o código de novo e não gerar erro de criação de coluna
            if(dtStep.Columns.Count <= 1)
            {
                //Colunas da tabela
                dtStep.Columns.Add("Endereço(s):");
                dtStep.Columns.Add("Valor:");

                //Preenche as colunas para caber no espaço do dataGrid
                dataGridView2.Columns[0].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
                dataGridView2.Columns[1].AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
            }

            string instructionName = dataGridView1.Rows[linhaInstrucaoStep].Cells[1].Value.ToString();
            string firstAttribute = dataGridView1.Rows[linhaInstrucaoStep].Cells[2].Value.ToString();
            string secondAttribute = dataGridView1.Rows[linhaInstrucaoStep].Cells[3].Value.ToString();

            if (dataGridView1.Rows[linhaInstrucaoStep].Cells[1].Value.ToString().Equals("START"))
            {
                dtStep.Clear();
                dataGridView1.ClearSelection();
                dataGridView1.Rows[linhaInstrucaoStep].Selected = true;

                topoDaPilhaStep = instructionStep.execute(instructionName, firstAttribute, secondAttribute, arrayStashStep, topoDaPilhaStep);
                linhaInstrucaoStep++;
            }
    
        }

        private void continueToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Coloca no dataGrid as colunas criadas
            dataGridView2.DataSource = dtStep;

            //Variaveis Gerais
            int i;
            string instructionName, firstAttribute, secondAttribute;

            if (topoDaPilhaStep != -99)
            {

                instructionName = dataGridView1.Rows[linhaInstrucaoStep].Cells[1].Value.ToString();
                firstAttribute = dataGridView1.Rows[linhaInstrucaoStep].Cells[2].Value.ToString();
                secondAttribute = dataGridView1.Rows[linhaInstrucaoStep].Cells[3].Value.ToString();

                if (instructionName.Equals("JMP") || instructionName.Equals("JMPF"))
                {
                    int newInstructionLine = instructionStep.executeJump(dataGridView1, instructionName, firstAttribute);
                    if (newInstructionLine != (-1))
                    {
                        linhaInstrucaoStep = newInstructionLine;
                    }
                    else
                    {
                        //Não encontrou a linha especificada no jump
                        //Verificar criação de exceções para cada tipo de erro possível na máquina virtual
                    }
                }
                else
                {
                    topoDaPilhaStep = instructionStep.execute(instructionName, firstAttribute, secondAttribute, arrayStashStep, topoDaPilhaStep);
                }

                if (dataGridView1.Rows[linhaInstrucaoStep].Cells[1].Value.ToString().Equals("HLT"))
                {
                    dataGridView1.ClearSelection();
                    dataGridView1.Rows[linhaInstrucaoStep].Selected = true;
                    topoDaPilhaStep = -99;
                    linhaInstrucaoStep = 0;
                }

                if (!(instructionName.Equals("START")) && !(instructionName.Equals("HLT")) && !(instructionName.Equals("JMP")))
                {
                    dataGridView1.ClearSelection();
                    dtStep.Clear();

                    for (i = 0; i < arrayStashStep.Count; i++)
                    {
                        dtStep.Rows.Add(i, arrayStashStep[i].ToString());
                    }

                    dataGridView2.ClearSelection();
                    dataGridView2.Rows[topoDaPilhaStep].Selected = true;

                    if (instructionName.Equals("PRN"))
                    {
                        richTextBox4.AppendText(arrayStashStep[topoDaPilhaStep].ToString() + "\n");
                        topoDaPilhaStep--;
                    }

                    dataGridView1.Rows[linhaInstrucaoStep].Selected = true;
                    linhaInstrucaoStep++;
                }
                
            }

        }

        private void stopToolStripMenuItem_Click(object sender, EventArgs e)
        {
            topoDaPilhaStep = -99;
            linhaInstrucaoStep = 0;

            dtStep.Clear();
            dataGridView1.ClearSelection();
            dataGridView1.Rows[0].Selected = true;
        }
    }

    public class Instruction
    {
        public int executeJump(DataGridView file, String instruction, String line)
        {
            int i = 0;
            while(!file.Rows[i].Cells[1].Value.ToString().Equals("HLT"))
            {
                if(file.Rows[i].Cells[1].Value.ToString().Equals(line))
                {
                    return i;
                }
                i++;
            }
            return -1;
        }

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
                        array.Add(Convert.ToInt32(firstAttribute));
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

                    case "NULL":
                        return topoPilha;

                    case "STR":
                        if(Convert.ToInt32(firstAttribute) >= array.Count)
                        {
                            array.Add(array[topoPilha]);
                        }
                        else
                        {
                            array[Convert.ToInt32(firstAttribute)] = array[topoPilha];
                        }
                        return topoPilha - 1;

                    case "PRN":
                        return topoPilha;

                    case "AND":
                        if(array[topoPilha - 1].Equals(1) && array[topoPilha].Equals(1))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        return topoPilha - 1;

                    case "OR":
                        if (array[topoPilha - 1].Equals(1) || array[topoPilha].Equals(1))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        return topoPilha - 1;

                    case "NEG":
                        array[topoPilha] = 1 - Convert.ToInt32(array[topoPilha]);
                        return topoPilha;

                    case "CME":
                        if (Convert.ToInt32(array[topoPilha - 1]) < Convert.ToInt32(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        return topoPilha - 1;

                    case "CMEQ":
                        if (Convert.ToInt32(array[topoPilha - 1]) <= Convert.ToInt32(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        return topoPilha - 1;

                    case "CMA":
                        if (Convert.ToInt32(array[topoPilha - 1]) > Convert.ToInt32(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        return topoPilha - 1;

                    case "CMAQ":
                        if (Convert.ToInt32(array[topoPilha - 1]) >= Convert.ToInt32(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        return topoPilha - 1;


                    case "CEQ":
                        if (array[topoPilha - 1].Equals(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        return topoPilha - 1;

                    case "CDIF":
                        if (!array[topoPilha - 1].Equals(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        return topoPilha - 1;

                    case "HLT":
                        array.Clear();
                        return -99;

                    default:
                        if (String.Equals(firstAttribute,"NULL"))
                        {
                            topoPilha = execute(firstAttribute, "", "", array, topoPilha);
                            return topoPilha;
                        }
                        return -99;

                }
            }
        }
    }
}

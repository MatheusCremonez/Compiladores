using Microsoft.VisualStudio.TestTools.UnitTesting;
using VirtualMachine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections;

namespace VirtualMachine.Tests
{
    [TestClass()]
    public class InterfaceVMTests
    {
        [TestMethod()]
        public void InterfaceVMTest()
        {
            interfaceTest();
            instructionTest();
        }

        public void interfaceTest()
        {
            InterfaceVM myInterface = new InterfaceVM();
            //teste para verificar se a interface foi criada corretamente
            Assert.IsNotNull(myInterface);
        }

        public void instructionTest()
        {
            Instruction instruction = new Instruction();
            int arrayTop = 0;
            ArrayList mockArray = new ArrayList();

            instructionStartTest(instruction);
            instructionHltTest(instruction);

            instructionLdcTest(instruction, arrayTop, mockArray);
            instructionLdvTest(instruction, arrayTop, mockArray);

            instructionAddTest(instruction, arrayTop, mockArray);
            instructionSubTest(instruction, arrayTop, mockArray);
            instructionMultTest(instruction, arrayTop, mockArray);
            instructionDiviTest(instruction, arrayTop, mockArray);
            instructionInvTest(instruction, arrayTop, mockArray);

            instructionAndTest(instruction, arrayTop, mockArray);
            instructionOrTest(instruction, arrayTop, mockArray);
            instructionNegTest(instruction, arrayTop, mockArray);

            instructionCmeTest(instruction, arrayTop, mockArray);
            instructionCmeqTest(instruction, arrayTop, mockArray);
            instructionCmaTest(instruction, arrayTop, mockArray);
            instructionCmaqTest(instruction, arrayTop, mockArray);
            instructionCeqTest(instruction, arrayTop, mockArray);
            instructionCdifTest(instruction, arrayTop, mockArray);
        }

        public void instructionStartTest(Instruction instruction)
        {
            int newArrayTop = instruction.execute("START", "", "", null, 0);
            Assert.AreEqual(-1, newArrayTop);
        }

        public void instructionHltTest(Instruction instruction)
        {
            int newArrayTop = instruction.execute("HLT", "", "", null, 0);
            Assert.AreEqual(-99, newArrayTop);
        }

        public void instructionLdcTest(Instruction instruction, int top, ArrayList array)
        {
            String attribute = "1";
            top = -1;
            int newArrayTop = instruction.execute("LDC", attribute, "", array, top);
            Assert.AreEqual(top+1, newArrayTop);
            Assert.AreEqual(array[newArrayTop], Convert.ToInt32(attribute));

            array.Clear();
        }

        public void instructionLdvTest(Instruction instruction, int top, ArrayList array)
        {
            String attribute = "1";
            array.Add(10);
            array.Add(20);
            top = 1;

            int newArrayTop = instruction.execute("LDV", attribute, "", array, top);
            Assert.AreEqual(top + 1, newArrayTop);
            Assert.AreEqual(array[newArrayTop], array[Convert.ToInt32(attribute) + 1]);

            array.Clear();
        }

        public void instructionAddTest(Instruction instruction, int top, ArrayList array)
        {
            array.Add(10);
            array.Add(2);
            top = 1;

            int newArrayTop = instruction.execute("ADD", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(12, array[newArrayTop]);

            array.Clear();
        }

        public void instructionSubTest(Instruction instruction, int top, ArrayList array)
        {
            array.Add(2);
            array.Add(10);
            top = 1;

            int newArrayTop = instruction.execute("SUB", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(8, array[newArrayTop]);

            array.Clear();
        }

        public void instructionMultTest(Instruction instruction, int top, ArrayList array)
        {
            array.Add(10);
            array.Add(2);
            top = 1;

            int newArrayTop = instruction.execute("MULT", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(20, array[newArrayTop]);

            array.Clear();
        }

        public void instructionDiviTest(Instruction instruction, int top, ArrayList array)
        {
            array.Add(10);
            array.Add(20);
            top = 1;

            int newArrayTop = instruction.execute("DIVI", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(2, array[newArrayTop]);

            array.Clear();
        }

        public void instructionInvTest(Instruction instruction, int top, ArrayList array)
        {
            array.Add(1);
            top = 0;

            int newArrayTop = instruction.execute("INV", "", "", array, top);
            Assert.AreEqual(top, newArrayTop);
            Assert.AreEqual(-1, array[newArrayTop]);

            array.Clear();
        }

        public void instructionAndTest(Instruction instruction, int top, ArrayList array)
        {
            int newArrayTop;

            for(int i = 0; i < 2; i++)
            {
                for (int j = 0; i < 2; i++)
                {
                    array.Add(i);
                    array.Add(j);

                    top = 1;

                    newArrayTop = instruction.execute("AND", "", "", array, top);
                    Assert.AreEqual(top-1, newArrayTop);
                    Assert.AreEqual(i*j, array[newArrayTop]);

                    array.Clear();
                }
            }
           
        }

        public void instructionOrTest(Instruction instruction, int top, ArrayList array)
        {
            int newArrayTop;

            for (int i = 0; i < 2; i++)
            {
                for (int j = 0; i < 2; i++)
                {
                    array.Add(i);
                    array.Add(j);

                    top = 1;

                    newArrayTop = instruction.execute("OR", "", "", array, top);
                    Assert.AreEqual(top - 1, newArrayTop);
                    Assert.AreEqual(i + j, array[newArrayTop]);

                    array.Clear();
                }
            }

        }

        public void instructionNegTest(Instruction instruction, int top, ArrayList array)
        {
            int value = 2;
            array.Add(value);
            top = 0;

            int newArrayTop = instruction.execute("NEG", "", "", array, top);
            Assert.AreEqual(top, newArrayTop);
            Assert.AreEqual(1 - value, array[newArrayTop]);

            array.Clear();
        }

        public void instructionCmeTest(Instruction instruction, int top, ArrayList array)
        {
            int firstValue, secondValue, newArrayTop, logicFalse = 0, logicTrue = 1;
            top = 1;

            // firstValue < secondValue => TRUE (1)
            firstValue = 1;
            secondValue = 2;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CME", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicTrue, array[newArrayTop]);

            array.Clear();

            // firstValue >= secondValue => FALSE (0)
            firstValue = 3;
            secondValue = 2;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CME", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicFalse, array[newArrayTop]);

            array.Clear();
        }

        public void instructionCmeqTest(Instruction instruction, int top, ArrayList array)
        {
            int firstValue, secondValue, newArrayTop, logicFalse = 0, logicTrue = 1;
            top = 1;

            // firstValue <= secondValue => TRUE (1)
            firstValue = 1;
            secondValue = 1;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CMEQ", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicTrue, array[newArrayTop]);

            array.Clear();

            // firstValue > secondValue => FALSE (0)
            firstValue = 3;
            secondValue = 2;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CMEQ", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicFalse, array[newArrayTop]);

            array.Clear();
        }

        public void instructionCmaTest(Instruction instruction, int top, ArrayList array)
        {
            int firstValue, secondValue, newArrayTop, logicFalse = 0, logicTrue = 1;
            top = 1;

            // firstValue <= secondValue => FALSE (0)
            firstValue = 1;
            secondValue = 2;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CMA", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicFalse, array[newArrayTop]);

            array.Clear();

            // firstValue > secondValue => TRUE (1)
            firstValue = 3;
            secondValue = 2;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CMA", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicTrue, array[newArrayTop]);

            array.Clear();
        }

        public void instructionCmaqTest(Instruction instruction, int top, ArrayList array)
        {
            int firstValue, secondValue, newArrayTop, logicFalse = 0, logicTrue = 1;
            top = 1;

            // firstValue < secondValue => FALSE (0)
            firstValue = 1;
            secondValue = 2;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CMAQ", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicFalse, array[newArrayTop]);

            array.Clear();

            // firstValue >= secondValue => TRUE (1)
            firstValue = 3;
            secondValue = 2;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CMAQ", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicTrue, array[newArrayTop]);

            array.Clear();
        }

        public void instructionCeqTest(Instruction instruction, int top, ArrayList array)
        {
            int firstValue, secondValue, newArrayTop, logicFalse = 0, logicTrue = 1;
            top = 1;

            // firstValue != secondValue => FALSE (0)
            firstValue = 1;
            secondValue = 2;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CEQ", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicFalse, array[newArrayTop]);

            array.Clear();

            // firstValue == secondValue => TRUE (1)
            firstValue = 3;

            array.Add(firstValue);
            array.Add(firstValue);

            newArrayTop = instruction.execute("CEQ", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicTrue, array[newArrayTop]);

            array.Clear();
        }

        public void instructionCdifTest(Instruction instruction, int top, ArrayList array)
        {
            int firstValue, secondValue, newArrayTop, logicFalse = 0, logicTrue = 1;
            top = 1;

            // firstValue != secondValue => TRUE (1)
            firstValue = 1;
            secondValue = 2;

            array.Add(firstValue);
            array.Add(secondValue);

            newArrayTop = instruction.execute("CDIF", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicTrue, array[newArrayTop]);

            array.Clear();

            // firstValue == secondValue => FALSE (0)
            firstValue = 3;

            array.Add(firstValue);
            array.Add(firstValue);

            newArrayTop = instruction.execute("CDIF", "", "", array, top);
            Assert.AreEqual(top - 1, newArrayTop);
            Assert.AreEqual(logicFalse, array[newArrayTop]);

            array.Clear();
        }
    }
}
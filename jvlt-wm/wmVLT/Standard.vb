Imports ICSharpCode.SharpZipLib.Zip
Imports System.IO
Imports System.Xml


Public Class Standard
    Private _parent As Startpage

    Private _dict As Dictionary
    Private _stats As Stats
    Private _repeatDict As Dictionary
    Private _allEntries As Hashtable
    Private _iterator As Collections.IDictionaryEnumerator
    Private _progress As Integer = 0

    Private _filenameRepeat As String


    Public Sub Init(ByRef parent As Startpage, ByVal dict As Dictionary, ByVal stat As Stats)
        _parent = parent
        _dict = dict
        _stats = stat
        lblDict.Text = _dict.language + " (" + _dict.version + ")"
        _allEntries = _dict.entries
        _iterator = _allEntries.GetEnumerator
        _repeatDict = New Dictionary(_dict.language, _dict.version)

        cbxLesson.DataSource = _dict.getLessons()
        nextVocab()
    End Sub

    Private Sub MenuItem1_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles menOptions.Click
        
    End Sub

    Private Sub menNext_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles menShow.Click

        showVocab()

    End Sub

    Private Sub cbxLesson_SelectedIndexChanged(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cbxLesson.SelectedIndexChanged
        If cbxLesson.SelectedItem.Equals("All Vocabs") Then
            _allEntries = _dict.entries
        Else
            _allEntries = _dict.getEntriesForLesson(cbxLesson.SelectedItem)
        End If
        _iterator = _allEntries.GetEnumerator
        _progress = 0
        nextVocab()
    End Sub

    Private Sub menStart_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles menStart.Click
        _parent.Show()
        _parent.BringToFront()
    End Sub

    Private Sub menRepeat_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles menRepeat.Click
        OpenFileDialog1.ShowDialog()
        _filenameRepeat = OpenFileDialog1.FileName

        If _filenameRepeat IsNot Nothing Then
            Dim strmZipInputStream As ZipInputStream = New ZipInputStream(File.OpenRead(_filenameRepeat))
            Dim objEntry As ZipEntry

            objEntry = strmZipInputStream.GetNextEntry()

            While IsNothing(objEntry) = False

                If objEntry.IsFile And objEntry.Name.StartsWith("dict") Then

                    Dim xmld As XmlDocument
                    Dim dic As XmlNode
                    Dim entrylist As XmlNodeList
                    Dim entry As XmlNode
                    Dim senselist As XmlNodeList
                    Dim sense As XmlNode

                    xmld = New XmlDocument()

                    xmld.Load(strmZipInputStream)

                    dic = xmld.DocumentElement

                    Dim dicLanguage = dic.Attributes.GetNamedItem("language").Value
                    Dim dicVersion = dic.Attributes.GetNamedItem("version").Value
                    _repeatDict = New Dictionary(dicLanguage, dicVersion)


                    entrylist = xmld.SelectNodes("/dictionary/entry")

                    For Each entry In entrylist

                        Dim entryId = entry.Attributes.GetNamedItem("id").Value
                        Dim entryChildren = entry.ChildNodes

                        Dim entryLesson = entry.LastChild.InnerText
                        Dim entryOrth = entry.FirstChild.InnerText

                        Dim ent As Entry = New wmVLT.Entry(entryId, entryOrth, entryLesson)

                        senselist = entry.SelectNodes("sense")

                        For Each sense In senselist
                            Dim senseId = sense.Attributes.GetNamedItem("id").Value
                            Dim senseTrans = sense.FirstChild.InnerText
                            Dim senseDef = ""
                            If sense.ChildNodes.Count > 1 Then
                                senseDef = sense.LastChild.InnerText
                            End If

                            Dim sen As wmVLT.Sense = New wmVLT.Sense(senseId, senseTrans, senseDef)

                            ent.addSense(sen)
                        Next
                        _repeatDict.addEntry(ent)
                    Next

                End If

                'End If

                objEntry = strmZipInputStream.GetNextEntry()
            End While

            strmZipInputStream.Close()
            menSaveRepeat.Enabled = True
        End If

    End Sub

    Private Sub menSaveRepeat_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles menSaveRepeat.Click


        If _filenameRepeat IsNot Nothing Then
            Dim xmld As XmlDocument = New XmlDocument
            xmld.LoadXml(_repeatDict.toXML.ToString)

            Dim strmZipOut As ZipOutputStream = New ZipOutputStream(File.OpenWrite(_filenameRepeat))
            Dim outEntry As ZipEntry = New ZipEntry("dict.xml")
            strmZipOut.PutNextEntry(outEntry)

            Dim sw As StringWriter = New StringWriter()
            Dim xw As XmlTextWriter = New XmlTextWriter(sw)

            xmld.WriteTo(xw)
            Dim encoding As System.Text.UTF8Encoding = New System.Text.UTF8Encoding

            Dim xmldAsBytes() As Byte = encoding.GetBytes(sw.ToString())

            strmZipOut.Write(xmldAsBytes, 0, xmldAsBytes.Length)
            strmZipOut.CloseEntry()
            strmZipOut.Close()
        Else
            SaveFileDialog1.ShowDialog()

            _filenameRepeat = SaveFileDialog1.FileName


            Dim xmld As XmlDocument = New XmlDocument
            xmld.LoadXml(_repeatDict.toXML.ToString)

            Dim strmZipOut As ZipOutputStream = New ZipOutputStream(File.OpenWrite(_filenameRepeat))
            Dim outEntry As ZipEntry = New ZipEntry("dict.xml")
            strmZipOut.PutNextEntry(outEntry)

            Dim sw As StringWriter = New StringWriter()
            Dim xw As XmlTextWriter = New XmlTextWriter(sw)

            xmld.WriteTo(xw)
            Dim encoding As System.Text.UTF8Encoding = New System.Text.UTF8Encoding
            Dim xmldAsBytes() As Byte = encoding.GetBytes(sw.ToString())

            strmZipOut.Write(xmldAsBytes, 0, xmldAsBytes.Length)
            strmZipOut.CloseEntry()
            strmZipOut.Close()
        End If

    End Sub

    Private Sub btnPlus_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btnPlus.Click
        Dim currEntry As Entry = _iterator.Value
        _repeatDict.addEntry(currEntry)

    End Sub


    Private Sub btnMinus_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btnMinus.Click
        Dim currEntry As Entry = _iterator.Value
        If _allEntries.Contains(currEntry.id) Then
            _repeatDict.removeEntry(currEntry)
        End If
    End Sub

    Private Sub btnRight_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btnRight.Click
        nextVocab()
        Dim currEntry As Entry = _iterator.Value
        Dim info As EntryInfo = _stats.enrtyInfos.Item(currEntry.id)
        info.lastQueried = TimeOfDay
        info.queried = info.queried + 1
    End Sub

    Public Sub showVocab()
        If _allEntries.Count > 0 Then
            Dim currEntry As Entry = _iterator.Value

            lblLeccion.Text = currEntry.lesson
            If Not chkDirection.Checked Then
                lblVocab.Text = currEntry.orth
            Else
                Dim output As String = ""
                Dim counter As Integer = 1
                For Each sen As Sense In currEntry.senses
                    If currEntry.senses.Count > 1 Then
                        output = output + counter.ToString + ". " + sen.trans + System.Environment.NewLine
                    Else
                        output = output + sen.trans + System.Environment.NewLine
                    End If

                    counter = counter + 1
                Next
                lblVocab.Text = output
            End If
        End If
    End Sub

    Private Sub nextVocab()
        pgrVocabs.Minimum = 0
        pgrVocabs.Maximum = _allEntries.Count
        If _iterator.MoveNext Then
            _progress = _progress + 1
            Dim currEntry As Entry = _iterator.Value

            If _repeatDict.entries.Contains(currEntry.id) Then
                Panel1.BackColor = Color.Red
            Else
                Panel1.BackColor = Color.White
            End If

            If Not chkDirection.Checked Then
                Dim output As String = ""
                Dim counter As Integer = 1
                For Each sen As Sense In currEntry.senses
                    If currEntry.senses.Count > 1 Then
                        output = output + counter.ToString + ". " + sen.trans + System.Environment.NewLine
                    Else
                        output = output + sen.trans + System.Environment.NewLine
                    End If

                    counter = counter + 1
                    pgrVocabs.Value = _progress
                Next
                lblForeignVocab.Text = output
            Else
                lblForeignVocab.Text = currEntry.orth
            End If

            lblVocab.Text = ""
        Else
            Windows.Forms.MessageBox.Show("Lesson finished. This Lesson will start again, beginning with it's first Vocab.", "Lesson finished", MessageBoxButtons.OK, MessageBoxIcon.Exclamation, MessageBoxDefaultButton.Button1)
            _iterator.Reset()
            _progress = 0
            nextVocab()
        End If
         
    End Sub

    Private Sub btnWrong_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btnWrong.Click
        nextVocab()
        Dim currEntry As Entry = _iterator.Value
        Dim info As EntryInfo = _stats.enrtyInfos.Item(currEntry.id)
        info.mistakes = info.mistakes + 1
        info.lastQueried = TimeOfDay
        info.queried = info.queried + 1
    End Sub
End Class

Imports System.Xml

Public Class Sense
    '    <xs:element name="sense">
    '  <xs:complexType>
    '      <xs:sequence>
    '        <xs:element ref="trans"/>
    '        <xs:element ref="def" minOccurs="0"/>
    '      </xs:sequence>
    '    <xs:attribute name="id" type="xs:string" use="required"/>
    '  </xs:complexType>
    '</xs:element>

    Private _id As String
    Private _trans As String
    Private _def As String

    Public Property id() As String
        Get
            Return Me._id
        End Get
        Set(ByVal value As String)
            Me._id = value
        End Set
    End Property

    Public Property trans() As String
        Get
            Return Me._trans
        End Get
        Set(ByVal value As String)
            Me._trans = value
        End Set
    End Property

    Public Property def() As String
        Get
            Return _def
        End Get
        Set(ByVal value As String)
            Me._def = value
        End Set
    End Property


    Public Sub New(ByVal id As String, ByVal trans As String, ByVal def As String)
        Me.id = id
        Me.trans = trans
        Me.def = def
    End Sub

    Public Sub New(ByVal id As String, ByVal trans As String)
        Me.id = id
        Me.trans = trans
        Me.def = ""
    End Sub

    Public Function toXML() As XElement
        Dim idAtt As XAttribute = New XAttribute("id", Me.id)
        Dim ret As XElement = <sense>
                                  <trans><%= Me.trans %></trans>
                                  <def><%= Me.def %></def>
                              </sense>
        ret.Add(idAtt)
        Return ret
    End Function

End Class
